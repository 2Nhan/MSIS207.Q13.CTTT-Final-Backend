package com.crm.project.service;

import com.crm.project.dto.request.QuotationCreationRequest;
import com.crm.project.dto.request.QuotationItemRequest;
import com.crm.project.dto.response.QuotationResponse;
import com.crm.project.entity.*;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.internal.QuotationItemInfo;
import com.crm.project.mapper.LeadMapper;
import com.crm.project.mapper.QuotationMapper;
import com.crm.project.mapper.UserMapper;
import com.crm.project.repository.*;
import com.crm.project.utils.CalculatorUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final QuotationMapper quotationMapper;
    private final UserMapper userMapper;
    private final LeadMapper leadMapper;
    private final MailService mailService;
    private final PdfService pdfService;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public QuotationResponse createQuotation(QuotationCreationRequest request) {

        Lead lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new AppException(ErrorCode.LEAD_NOT_FOUND));

        Map<Product, Integer> productQuantityMap = getProductFromItemRequest(request.getItems());

        List<QuotationItemInfo> itemInfos = getQuotationItemInfo(productQuantityMap);

        List<QuotationItem> quotationItems = new ArrayList<>();
        Map<String, Product> productMap = productQuantityMap.keySet().stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (QuotationItemInfo info : itemInfos) {
            QuotationItem item = quotationMapper.toQuotationItem(info);

            Product product = productMap.get(info.getProductId());
            if (product == null) {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            item.setProduct(product);
            quotationItems.add(item);
        }

        User user = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        BigDecimal total = CalculatorUtil.calculateQuotationTotal(itemInfos);

        Quotation quotation = Quotation.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .validUntil(request.getValidUntil())
                .status("Draft")
                .lead(lead)
                .createdBy(user)
                .total(total)
                .finalTotal(total.multiply(BigDecimal.valueOf(1.1)))
                .build();

        if (request.getValidUntil() == null) {
            quotation.setValidUntil(LocalDate.now().plusDays(7));
        }

        quotationItems.forEach(item -> item.setQuotation(quotation));
        quotation.setItems(quotationItems);

        quotationRepository.save(quotation);

        return QuotationResponse.builder()
                .id(quotation.getId())
                .lead(leadMapper.toLeadQuotationInfo(lead))
                .title(quotation.getTitle())
                .content(quotation.getContent())
                .validUntil(quotation.getValidUntil())
                .status(quotation.getStatus())
                .total(quotation.getTotal())
                .items(itemInfos)
                .createdBy(userMapper.toUserNormalInfo(user))
                .createdAt(quotation.getCreatedAt())
                .updatedAt(quotation.getUpdatedAt())
                .build();
    }

    @Transactional
    public void sendQuotationEmail(String id) throws Exception {
        Quotation quotation = quotationRepository.findQuotationDetailById(id).orElseThrow(() -> new AppException(ErrorCode.QUOTATION_NOT_FOUND));

        List<QuotationItem> quotationItems = quotation.getItems();
        List<String> products = new ArrayList<>();
        for (QuotationItem item : quotationItems) {
            products.add(item.getProduct().getName());
        }

        String to = quotation.getLead().getEmail();
        String receiver = quotation.getLead().getFullName();

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String sender = user.getFirstName() + " " + user.getLastName();

        LocalDate startDate = quotation.getCreatedAt().toLocalDate();
        LocalDate endDate = quotation.getValidUntil();

        byte[] attachment = pdfService.generateQuotation(receiver, sender, startDate, endDate, quotation);

        mailService.sendQuotationMail(to, sender, receiver, products, attachment);

        quotationRepository.updateStatusToSent(quotation.getId());

        try {
            String fileName = "quotation_" + quotation.getId() + ".pdf";
            String folder = "project/pdfs";

            cloudinaryService.uploadAndUpdateRecord(
                    attachment,
                    fileName,
                    folder,
                    quotation.getId()
            );

            log.info("Started async upload of Quotation PDF [{}] to Cloudinary...", fileName);
        } catch (Exception ex) {
            log.error("Failed to trigger Cloudinary upload for quotation [{}]: {}", quotation.getId(), ex.getMessage());
        }
    }

    public QuotationResponse getQuotation(String id) {
        Quotation quotation = quotationRepository.findQuotationDetailById(id).orElseThrow(() -> new AppException(ErrorCode.QUOTATION_NOT_FOUND));
        return quotationMapper.toQuotationResponse(quotation);
    }

    public Page<QuotationResponse> getAllQuotations(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<Quotation> quotations = quotationRepository.findAllQuotationsWithDetails(pageable);
        return quotations.map(quotationMapper::toQuotationResponse);
    }

    private Map<Product, Integer> getProductFromItemRequest(List<QuotationItemRequest> items) {
        Map<String, Integer> quantities = items.stream()
                .collect(Collectors.toMap(QuotationItemRequest::getId, QuotationItemRequest::getQuantity));

        List<Product> products = productRepository.findAllById(quantities.keySet());
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return products.stream()
                .collect(Collectors.toMap(Function.identity(), p -> quantities.get(p.getId())));
    }

    private List<QuotationItemInfo> getQuotationItemInfo(Map<Product, Integer> items) {
        List<QuotationItemInfo> result = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            Product product = entry.getKey();
            Integer qty = entry.getValue();

            QuotationItemInfo info = new QuotationItemInfo();
            info.setProductId(product.getId());
            info.setName(product.getName());
            info.setDiscount(product.getDiscount());
            info.setDiscountType(product.getDiscountType());
            info.setUnitPrice(product.getPrice());
            info.setQuantity(qty);
            info.setSubtotal(CalculatorUtil.calculateItemSubtotal(info));

            result.add(info);
        }
        return result;
    }
}
