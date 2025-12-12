package com.crm.project.controller;

import com.crm.project.dto.request.ActivityCreationRequest;
import com.crm.project.dto.request.LeadCreationRequest;
import com.crm.project.dto.request.LeadUpdateRequest;
import com.crm.project.dto.request.MatchingRequest;
import com.crm.project.dto.response.*;

import com.crm.project.service.ActivityService;
import com.crm.project.service.LeadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public final ActivityService activityService;

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

    @PostMapping("/{id}/activities")
    public ResponseEntity<MyApiResponse> createLeadActivity(@PathVariable String id, @RequestBody @Valid ActivityCreationRequest request) {
        ActivityResponse response = activityService.createActivity(id, request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PatchMapping("/{leadId}/activities/{activityId}/complete")
    public ResponseEntity<MyApiResponse> markActivityCompleted(@PathVariable("leadId") String leadId, @PathVariable("activityId") String activityId) {
        ActivityResponse response = activityService.markActivityAsCompleted(leadId, activityId);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{leadId}/activities/{activityId}")
    public ResponseEntity<MyApiResponse> deleteActivity(@PathVariable("leadId") String leadId, @PathVariable("activityId") String activityId) {
        activityService.deleteActivity(leadId, activityId);
        MyApiResponse response = MyApiResponse.builder()
                .message("Activity deleted successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/csv")
    public ResponseEntity<MyApiResponse> importLeadCsv(@RequestPart MatchingRequest matching, @RequestPart("file") MultipartFile file) throws IOException {
        ImportResultResponse<LeadResponse> response = leadService.importLeadsFromCsv(matching, file);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(response.getValidList())
                .error(response.getInvalidList())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
