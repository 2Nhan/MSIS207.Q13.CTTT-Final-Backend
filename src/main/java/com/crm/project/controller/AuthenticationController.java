package com.crm.project.controller;

import com.crm.project.dto.request.LoginRequest;
import com.crm.project.dto.request.RefreshRequest;
import com.crm.project.dto.request.UserCreationRequest;
import com.crm.project.dto.response.MyApiResponse;
import com.crm.project.dto.response.LoginResponse;
import com.crm.project.dto.response.RefreshResponse;
import com.crm.project.dto.response.UserResponse;
import com.crm.project.redis.redishash.LogoutToken;
import com.crm.project.service.AuthenticationService;
import com.crm.project.service.UserService;
import com.crm.project.validator.group_sequences.ValidationSequences;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<MyApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authenticationService.login(loginRequest);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(loginResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<MyApiResponse> logout(HttpServletRequest request) throws ParseException {
        authenticationService.logout(request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .message("Logout successful")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/registration")
    public ResponseEntity<MyApiResponse> createUser(@RequestBody @Validated(ValidationSequences.class) UserCreationRequest request) {
        UserResponse userResponse = userService.createUser(request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(userResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<MyApiResponse> refreshToken(@Valid @RequestBody RefreshRequest request) {
        RefreshResponse refreshResponse = authenticationService.refresh(request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(refreshResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<MyApiResponse> get() {
        List<LogoutToken> logoutTokens = authenticationService.getAllBlacklistTokens();
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(logoutTokens)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
