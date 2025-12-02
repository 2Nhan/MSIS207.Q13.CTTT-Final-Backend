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
    @Operation(
            summary = "Create a new stage",
            description = "Add a new stage to the lead pipeline.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Stage creation request body",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "name": "Qualified"
                                    }
                                    """)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Stage created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 200,
                                      "message": "Process succeed",
                                      "data": {
                                        "id": "c8599c94-45dd-493d-b012-8495f7d6afe6",
                                        "name": "Qualified"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Stage already exists",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 400,
                                      "message": "Process Failed",
                                      "error": {
                                        "code": 1013,
                                        "message": "Stage already exists"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Process Failed",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 400,
                                      "message": "Processed Failed",
                                      "error": [
                                        {
                                          "code": 2014,
                                          "errorField": "name",
                                          "message": "Please fill in name"
                                        }
                                      ]
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<MyApiResponse> createStage(@RequestBody @Valid StageCreationRequest stageCreationRequest) {
        StageResponse stageResponse = stageService.createStage(stageCreationRequest);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(stageResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Rename a stage",
            description = "Update the name of a specific sales stage.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = @Parameter(
                    name = "id",
                    description = "Stage ID to update",
                    example = "53919da9-96e0-4b68-89fb-252d149919ff",
                    required = true
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Stage update request body",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "name": "Won"
                                    }
                                    """)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Stage updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 200,
                                      "message": "Process succeed",
                                      "data": {
                                        "id": "53919da9-96e0-4b68-89fb-252d149919ff",
                                        "name": "Won"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Stage not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 404,
                                      "message": "Process Failed",
                                      "error": {
                                        "code": 1010,
                                        "message": "Stage not found"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Can not make change on default stage",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 400,
                                      "message": "Process Failed",
                                      "error": {
                                        "code": 1014,
                                        "message": "Default stage is immutable"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Process Failed",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 400,
                                      "message": "Processed Failed",
                                      "error": [
                                        {
                                          "code": 2014,
                                          "errorField": "name",
                                          "message": "Please fill in name"
                                        }
                                      ]
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<MyApiResponse> updateStage(@PathVariable String id, @RequestBody @Valid StageUpdateRequest request) {
        StageResponse stageResponse = stageService.updateStage(id, request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(stageResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a stage",
            description = "Delete a stage by its ID from the sales pipeline.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = @Parameter(
                    name = "id",
                    description = "Stage ID to delete",
                    example = "d84eff8d-1efb-4c51-8e17-bf3963aadccb",
                    required = true
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Stage deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 200,
                                      "message": "Process succeed"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Stage not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 404,
                                      "message": "Process Failed",
                                      "error": {
                                        "code": 1010,
                                        "message": "Stage not found"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Can not make change on default stage",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 400,
                                      "message": "Process Failed",
                                      "error": {
                                        "code": 1014,
                                        "message": "Default stage is immutable"
                                      }
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<MyApiResponse> deleteStage(@PathVariable String id) {
        stageService.deleteStage(id);
        MyApiResponse apiResponse = MyApiResponse.builder().build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
