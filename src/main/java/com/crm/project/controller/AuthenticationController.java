package com.crm.project.controller;

import com.crm.project.dto.request.LoginRequest;
import com.crm.project.dto.response.ApiResponse;
import com.crm.project.dto.response.AuthenticationResponse;
import com.crm.project.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthenticationResponse authenticationResponse = authenticationService.login(loginRequest);
        ApiResponse apiResponse = ApiResponse.builder()
                .data(authenticationResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
