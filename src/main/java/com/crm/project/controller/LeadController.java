package com.crm.project.controller;

import com.crm.project.dto.request.LeadCreationRequest;
import com.crm.project.dto.request.LeadUpdateStageRequest;
import com.crm.project.dto.response.ApiResponse;
import com.crm.project.dto.response.LeadResponse;

import com.crm.project.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getLead(@PathVariable String id) {
        LeadResponse leadResponse = leadService.getLead(id);
        ApiResponse apiResponse = ApiResponse.builder()
                .data(leadResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/{id}/stage")
    public ResponseEntity<ApiResponse> updateLeadStage(@PathVariable("id") String id, @RequestBody @Valid LeadUpdateStageRequest request) {
        leadService.updateLeadStage(id, request);
        ApiResponse apiResponse = ApiResponse.builder()
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
