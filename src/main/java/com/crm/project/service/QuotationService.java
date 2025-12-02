package com.crm.project.service;

import com.crm.project.dto.request.QuotationCreationRequest;
import com.crm.project.dto.request.QuotationItemRequest;
import com.crm.project.dto.response.QuotationResponse;
import com.crm.project.entity.Lead;
import com.crm.project.entity.Product;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.internal.QuotationItemInfo;
import com.crm.project.repository.LeadRepository;
import com.crm.project.repository.ProductRepository;
import com.crm.project.repository.QuotationRepository;
import com.crm.project.repository.UserRepository;
import com.crm.project.utils.CalculatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class QuotationService {
    private final QuotationRepository quotationRepository;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

//    public QuotationResponse createQuotation(QuotationCreationRequest request) {
//        Lead lead = leadRepository.findById(request.getLeadId()).orElseThrow(() -> new AppException(ErrorCode.LEAD_NOT_FOUND));
//        Map<Product, Integer> itemsWithQuantity = getProductFromItemRequest(request.getItems());
//        List<Product> products = new ArrayList<>();
//    }

    private Map<Product, Integer> getProductFromItemRequest(List<QuotationItemRequest> items) {
        List<String> productIds = items.stream().map(QuotationItemRequest::getId).toList();

        List<Product> products = productRepository.findAllById(productIds);

        Map<String, Integer> temp = items.stream().collect(Collectors.toMap(QuotationItemRequest::getId, QuotationItemRequest::getQuantity));

        return products.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        product -> temp.get(product.getId())
                ));
    }

    private List<QuotationItemInfo> getQuotationItemInfo(Map<Product, Integer> items) {
        List<QuotationItemInfo> result = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            QuotationItemInfo info = new QuotationItemInfo();
            info.setId(entry.getKey().getId());
            info.setName(entry.getKey().getName());
            info.setDiscount(entry.getKey().getDiscount());
            info.setDiscountType(entry.getKey().getDiscountType());
            info.setUnitPrice(entry.getKey().getPrice());
            info.setQuantity(entry.getValue());
            info.setSubtotal(CalculatorUtil.calculateItemTotalPrice(info));
            result.add(info);
        }
        return result;
    }
}
