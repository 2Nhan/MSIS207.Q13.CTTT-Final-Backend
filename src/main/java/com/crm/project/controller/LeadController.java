package com.crm.project.controller;

import com.crm.project.dto.request.LeadCreationRequest;
import com.crm.project.dto.request.LeadUpdateRequest;
import com.crm.project.dto.request.LeadUpdateStageRequest;
import com.crm.project.dto.response.MyApiResponse;
import com.crm.project.dto.response.LeadResponse;

import com.crm.project.dto.response.StagesWithLeadsResponse;
import com.crm.project.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Leads",
        description = "APIs for managing customer leads and sales stages."
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {
    public final LeadService leadService;

    @PostMapping
    @Operation(
            summary = "Create a new lead",
            description = "Create a new lead record with optional image upload."
    )
    public ResponseEntity<MyApiResponse> createLead(@ModelAttribute(value = "data") @Valid LeadCreationRequest request) {
        LeadResponse leadResponse = leadService.createLead(request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(leadResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get lead details",
            description = "Retrieve detailed information of a specific lead by ID."
    )
    public ResponseEntity<MyApiResponse> getLead(@PathVariable String id) {
        LeadResponse leadResponse = leadService.getLead(id);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(leadResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/{id}/stage")
    @Operation(
            summary = "Update lead stage",
            description = "Update the sales stage of a specific lead."
    )
    public ResponseEntity<MyApiResponse> updateLeadStage(@PathVariable("id") String id, @RequestBody @Valid LeadUpdateStageRequest request) {
        leadService.updateLeadStage(id, request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping()
    @Operation(
            summary = "Get leads of all stages",
            description = "Retrieve all leads grouped by their sales stages."
    )
    public ResponseEntity<MyApiResponse> getStagesWithLeads() {
        List<StagesWithLeadsResponse> responses = leadService.getStagesWithLeads();
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(responses)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search leads",
            description = "Search leads by keyword (name, email, or phone)."
    )
    public ResponseEntity<MyApiResponse> searchLeads(@RequestParam("query") String query) {
        List<StagesWithLeadsResponse> result = leadService.searchLeads(query);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(result)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MyApiResponse> updateLead(@PathVariable String id, @RequestBody @Valid LeadUpdateRequest request) {
        LeadResponse leadResponse = leadService.updateLead(id, request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(leadResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MyApiResponse> deleteLead(@PathVariable String id) {
        leadService.deleteLead(id);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .message("Lead deleted successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
