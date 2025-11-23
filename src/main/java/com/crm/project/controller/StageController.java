package com.crm.project.controller;

import com.crm.project.dto.request.StageCreationRequest;
import com.crm.project.dto.request.StageRenameRequest;
import com.crm.project.dto.response.ApiResponse;
import com.crm.project.dto.response.StageResponse;
import com.crm.project.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> renameStage(@PathVariable String id, @RequestBody @Valid StageRenameRequest request) {
        StageResponse stageResponse = stageService.renameStage(id, request);
        ApiResponse apiResponse = ApiResponse.builder()
                .data(stageResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
