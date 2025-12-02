package com.crm.project.controller;

import com.crm.project.dto.request.QuotationCreationRequest;
import com.crm.project.dto.response.MyApiResponse;
import com.crm.project.dto.response.QuotationResponse;
import com.crm.project.service.QuotationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/quotations")
public class QuotationController {
    private final QuotationService quotationService;

    @PostMapping
    public ResponseEntity<MyApiResponse> createQuotation(@RequestBody @Valid QuotationCreationRequest request) {
        QuotationResponse response = quotationService.createQuotation(request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
