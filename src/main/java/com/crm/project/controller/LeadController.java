package com.crm.project.controller;

import com.crm.project.dto.request.LeadCreationRequest;
import com.crm.project.dto.request.LeadUpdateRequest;
import com.crm.project.dto.response.MyApiResponse;
import com.crm.project.dto.response.LeadResponse;

import com.crm.project.dto.response.StagesWithLeadsResponse;
import com.crm.project.service.LeadService;
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

    @GetMapping("/{id}")
    public ResponseEntity<MyApiResponse> getLead(@PathVariable String id) {
        LeadResponse leadResponse = leadService.getLead(id);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(leadResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/{id}/stages")
    public ResponseEntity<MyApiResponse> updateLeadStage(@PathVariable("id") String id, @RequestParam String stageId) {
        leadService.updateLeadStage(id, stageId);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping()
    public ResponseEntity<MyApiResponse> getStagesWithLeads() {
        List<StagesWithLeadsResponse> responses = leadService.getStagesWithLeads();
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(responses)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<MyApiResponse> searchLeads(@RequestParam("query") String query) {
        List<StagesWithLeadsResponse> result = leadService.searchLeads(query);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(result)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MyApiResponse> updateLead(@PathVariable String id, @ModelAttribute @Valid LeadUpdateRequest request) {
        LeadResponse leadResponse = leadService.updateLead(id, request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(leadResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<MyApiResponse> updateLeadAssignment(@PathVariable String id, @RequestParam String userId) {
        leadService.updateLeadAssignment(id, userId);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .message("Lead assignment updated")
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
