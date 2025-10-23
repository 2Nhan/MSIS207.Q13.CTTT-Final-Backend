package com.crm.project.service;

import com.crm.project.dto.request.UserCreationRequest;
import com.crm.project.dto.request.UserUpdateRequest;
import com.crm.project.dto.response.CloudinaryResponse;
import com.crm.project.dto.response.UserResponse;
import com.crm.project.entity.User;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.mapper.UserMapper;
import com.crm.project.repository.UserRepository;
import com.crm.project.utils.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public void uploadAvatar(MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        String fileName = UploadFileUtil.standardizeFileName(file.getOriginalFilename());
        UploadFileUtil.assertAllowed(file, UploadFileUtil.IMAGE_PATTERN);
        CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(file, fileName);
        user.setAvatarUrl(cloudinaryResponse.getUrl());
        userRepository.save(user);
    }

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXSITED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXSITED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> searchUsers(String keyword, Pageable pageable) {
        List<User> usersList = userRepository.findBySearch(keyword, pageable);
        if (usersList.isEmpty()) {
            throw new AppException(ErrorCode.NO_RESULTS);
        }
        return usersList.stream().map(userMapper::toUserResponse).toList();
    }

    public List<UserResponse> getAllUsers(Pageable pageable) {
        List<User> usersList = userRepository.findAll(pageable).getContent();
        if (usersList.isEmpty()) {
            throw new AppException(ErrorCode.NO_RESULTS);
        }
        return usersList.stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getSelfInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(UserUpdateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        userMapper.updateUser(request, user);
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        user.setDeleted(true);
        userRepository.save(user);
    }
}
