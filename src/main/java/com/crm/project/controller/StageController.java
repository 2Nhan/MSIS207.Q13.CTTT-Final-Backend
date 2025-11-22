package com.crm.project.controller;

import com.crm.project.dto.request.StageCreationRequest;
import com.crm.project.dto.response.ApiResponse;
import com.crm.project.dto.response.StageResponse;
import com.crm.project.service.StageService;
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
@RequestMapping("/stages")
public class StageController {
    private final StageService stageService;

    @PostMapping
    public ResponseEntity<ApiResponse> createStage(@RequestBody @Valid StageCreationRequest stageCreationRequest) {
        StageResponse stageResponse = stageService.createStage(stageCreationRequest);
        ApiResponse apiResponse = ApiResponse.builder()
                .data(stageResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
