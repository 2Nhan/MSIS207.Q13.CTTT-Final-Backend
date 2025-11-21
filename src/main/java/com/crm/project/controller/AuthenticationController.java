package com.crm.project.controller;

import com.crm.project.dto.request.LoginRequest;
import com.crm.project.dto.response.ApiResponse;
import com.crm.project.dto.response.AuthenticationResponse;
import com.crm.project.redis.redishash.LogoutToken;
import com.crm.project.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.text.ParseException;

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

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) throws ParseException {
        authenticationService.logout(request);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("Logout successful")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

//    @GetMapping
//    public ResponseEntity<ApiResponse> get() {
//        List<LogoutToken> logoutTokens = authenticationService.getAllBlacklistTokens();
//        ApiResponse apiResponse = ApiResponse.builder()
//                .data(logoutTokens)
//                .build();
//        return ResponseEntity.ok(apiResponse);
//    }
}
