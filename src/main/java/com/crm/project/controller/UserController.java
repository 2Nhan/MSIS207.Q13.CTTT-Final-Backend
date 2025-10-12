package com.crm.project.controller;

import com.crm.project.dto.request.UserCreationRequest;
import com.crm.project.dto.response.ApiResponse;
import com.crm.project.dto.response.UserResponse;
import com.crm.project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<ApiResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        UserResponse userResponse = userService.createUser(request);
        ApiResponse apiResponse = ApiResponse.builder()
                .result(userResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserResponse> userResponseList = userService.getAllUsers();
        ApiResponse apiResponse = ApiResponse.builder()
                .result(userResponseList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
