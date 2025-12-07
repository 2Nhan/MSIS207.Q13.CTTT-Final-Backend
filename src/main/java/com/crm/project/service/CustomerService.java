package com.crm.project.service;

import com.crm.project.dto.response.CustomerResponse;
import com.crm.project.entity.Lead;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.mapper.LeadMapper;
import com.crm.project.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;

    public CustomerResponse getCustomerDetails(String id) {
        Lead lead = leadRepository.getCustomerDetails(id).orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        return leadMapper.toCustomerResponse(lead);
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
}
