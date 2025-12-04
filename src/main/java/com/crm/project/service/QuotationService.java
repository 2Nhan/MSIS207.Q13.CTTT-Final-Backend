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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public QuotationResponse createQuotation(QuotationCreationRequest request) {

        Lead lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new AppException(ErrorCode.LEAD_NOT_FOUND));

        Map<Product, Integer> productQuantityMap = getProductFromItemRequest(request.getItems());

        List<QuotationItemInfo> itemInfos = getQuotationItemInfo(productQuantityMap);

        List<QuotationItem> quotationItems = new ArrayList<>();
        for (QuotationItemInfo info : itemInfos) {
            QuotationItem item = quotationMapper.toQuotationItem(info);

            Product product = productQuantityMap.keySet().stream()
                    .filter(p -> p.getId().equals(info.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

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
                .status("CREATED")
                .lead(lead)
                .createdBy(user)
                .total(total)
                .build();

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

    public void sendQuotationEmail(String id) {
        Quotation quotation = quotationRepository.findQuotationDetailById(id).orElseThrow(() -> new AppException(ErrorCode.QUOTATION_NOT_FOUND));
        List<QuotationItem> quotationItems = quotation.getItems();
        List<String> products = new ArrayList<>();
        for (QuotationItem item : quotationItems) {
            products.add(item.getProduct().getName());
        }
        String to = quotation.getLead().getEmail();
        String fullName = quotation.getLead().getFullName();
        mailService.sendQuotationMail(to, fullName, products);
    }

    public QuotationResponse getQuotation(String id) {
        Quotation quotation = quotationRepository.findQuotationDetailById(id).orElseThrow(() -> new AppException(ErrorCode.QUOTATION_NOT_FOUND));
        return quotationMapper.toQuotationResponse(quotation);
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
