package com.crm.project.controller;

import com.crm.project.dto.request.LeadCreationRequest;
import com.crm.project.dto.response.ApiResponse;
import com.crm.project.dto.response.LeadResponse;

import com.crm.project.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/leads")
public class LeadController {
    public final LeadService leadService;

    @PostMapping
    public ResponseEntity<ApiResponse> createLead(@RequestPart(value = "data") @Valid LeadCreationRequest request,
                                                  @RequestPart(value = "image", required = false) MultipartFile file) {
        LeadResponse leadResponse = leadService.createLead(request, file);
        ApiResponse apiResponse = ApiResponse.builder()
                .data(leadResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
