package com.crm.project.controller;

import com.crm.project.dto.request.StageCreationRequest;
import com.crm.project.dto.request.StageUpdateRequest;
import com.crm.project.dto.response.MyApiResponse;
import com.crm.project.dto.response.StageResponse;
import com.crm.project.service.StageService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Stages",
        description = "APIs for managing lead pipeline stages."
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stages")
public class StageController {
    private final StageService stageService;

    @PostMapping
    public ResponseEntity<MyApiResponse> createStage(@RequestBody @Valid StageCreationRequest stageCreationRequest) {
        StageResponse stageResponse = stageService.createStage(stageCreationRequest);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(stageResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MyApiResponse> updateStage(@PathVariable String id, @RequestBody @Valid StageUpdateRequest request) {
        StageResponse stageResponse = stageService.updateStage(id, request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(stageResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MyApiResponse> deleteStage(@PathVariable String id) {
        stageService.deleteStage(id);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .message("Stage deleted successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
