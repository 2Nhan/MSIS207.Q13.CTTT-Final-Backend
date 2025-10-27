package com.crm.project.controller;

import com.crm.project.dto.request.UserCreationRequest;
import com.crm.project.dto.request.UserUpdateRequest;
import com.crm.project.dto.response.ApiResponse;
import com.crm.project.dto.response.PageResponse;
import com.crm.project.dto.response.UserResponse;
import com.crm.project.service.UserService;
import com.crm.project.validator.group_sequences.ValidationSequences;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse> uploadAvatar(@RequestParam("image") MultipartFile file) {
        userService.uploadAvatar(file);
        ApiResponse apiResponse = ApiResponse.builder().message("Avatar uploaded").build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/registration")
    public ResponseEntity<ApiResponse> createUser(@RequestBody @Validated(ValidationSequences.class) UserCreationRequest request) {
        UserResponse userResponse = userService.createUser(request);
        ApiResponse apiResponse = ApiResponse.builder()
                .result(userResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers(@RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNumber,
                                                   @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize,
                                                   @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                   @RequestParam(required = false, defaultValue = "asc") String sortOrder) {

        Page<UserResponse> userResponseList = userService.getAllUsers(pageNumber, pageSize, sortBy, sortOrder);

        ApiResponse apiResponse = ApiResponse.builder()
                .result(new PageResponse<>(userResponseList))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchUser(@RequestParam("query") String query,
                                                  @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNumber,
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize) {
        Page<UserResponse> userResponseList = userService.searchUsers(query, PageRequest.of(pageNumber - 1, pageSize));
        ApiResponse apiResponse = ApiResponse.builder()
                .result(new PageResponse<>(userResponseList))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse> getSelfInfo() {
        UserResponse userResponse = userService.getSelfInfo();
        ApiResponse apiResponse = ApiResponse.builder()
                .result(userResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable("id") String id) {
        UserResponse userResponse = userService.getUserById(id);
        ApiResponse apiResponse = ApiResponse.builder()
                .result(userResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping
    public ResponseEntity<ApiResponse> updateSelfInfo(@RequestBody @Valid UserUpdateRequest request) {
        UserResponse userResponse = userService.updateSelfInfo(request);
        ApiResponse apiResponse = ApiResponse.builder().result(userResponse).build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("DELETED USER")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
