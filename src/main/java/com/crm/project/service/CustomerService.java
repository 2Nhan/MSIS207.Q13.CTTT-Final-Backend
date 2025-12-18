package com.crm.project.service;

import com.crm.project.dto.response.CustomerResponse;
import com.crm.project.entity.Lead;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.internal.OrderStatisticInfo;
import com.crm.project.mapper.LeadMapper;
import com.crm.project.repository.LeadRepository;
import com.crm.project.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;
    private final OrderRepository orderRepository;

    public CustomerResponse getCustomerDetails(String id) {
        Lead lead = leadRepository.getCustomerDetails(id).orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        OrderStatisticInfo s = orderRepository.getOrderStatisticsByCustomer(id);
        if (s.getTotalOrders() == null) s.setTotalOrders(0L);
        if (s.getTotalSpent() == null) s.setTotalSpent(BigDecimal.ZERO);
        if (s.getAverageOrder() == null) s.setAverageOrder(0.0);
        if (s.getLastOrderAt() == null) s.setLastOrderAt(null);
        CustomerResponse customerResponse = leadMapper.toCustomerResponse(lead);
        customerResponse.setOrderStatisticInfo(s);
        return customerResponse;
    }

    public Page<CustomerResponse> getAllCustomers(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Lead> customerList = leadRepository.findAllCustomer(pageable);
        if (customerList.isEmpty()) {
            throw new AppException(ErrorCode.NO_RESULTS);
        }
        return customerList.map(leadMapper::toCustomerResponse);
    }

    @Transactional
    public void deleteCustomer(String id) {
        Lead lead = leadRepository.getCustomerDetails(id).orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        leadRepository.delete(lead);
    }

    public Page<CustomerResponse> searchCustomers(String query, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Lead> customerList = leadRepository.findCustomersBySearch(query, pageable);
        if (customerList.isEmpty()) {
            throw new AppException(ErrorCode.NO_RESULTS);
        }
        return customerList.map(leadMapper::toCustomerResponse);
    }
}
