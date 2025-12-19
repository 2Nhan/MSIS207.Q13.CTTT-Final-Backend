package com.crm.project.controller;

import com.crm.project.dto.request.UserUpdateRequest;
import com.crm.project.dto.response.*;
import com.crm.project.internal.PageInfo;
import com.crm.project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PatchMapping("/avatar")
    public ResponseEntity<MyApiResponse> uploadAvatar(@RequestParam("image") MultipartFile file) {
        ImageResponse imageResponse = userService.uploadAvatar(file);
        MyApiResponse apiResponse = MyApiResponse.builder().data(imageResponse).build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<MyApiResponse> getAllUsers(@RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNumber,
                                                     @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                     @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                     @RequestParam(required = false, defaultValue = "asc") String sortOrder) {

        Page<UserResponse> userResponseList = userService.getAllUsers(pageNumber, pageSize, sortBy, sortOrder);

        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(userResponseList.getContent())
                .pagination(new PageInfo<>(userResponseList))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<MyApiResponse> searchUser(@RequestParam("query") String query,
                                                    @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNumber,
                                                    @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        Page<UserResponse> userResponseList = userService.searchUsers(query, PageRequest.of(pageNumber - 1, pageSize));
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(userResponseList.getContent())
                .pagination(new PageInfo<>(userResponseList))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/info")
    public ResponseEntity<MyApiResponse> getSelfInfo() {
        UserResponse userResponse = userService.getSelfInfo();
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(userResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MyApiResponse> getUserById(@PathVariable("id") String id) {
        UserResponse userResponse = userService.getUserById(id);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(userResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping
    public ResponseEntity<MyApiResponse> updateSelfInfo(@RequestBody @Valid UserUpdateRequest request) {
        UserResponse userResponse = userService.updateSelfInfo(request);
        MyApiResponse apiResponse = MyApiResponse.builder().data(userResponse).build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MyApiResponse> deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .message("DELETED USER")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/assigners")
    public ResponseEntity<MyApiResponse> getAssigners() {
        List<UserAssignResponse> list = userService.getUserAssignList();
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(list)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<MyApiResponse> getUserDetails(@PathVariable("id") String id) {
        UserDetailResponse userDetail = userService.getUserDetails(id);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(userDetail)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
