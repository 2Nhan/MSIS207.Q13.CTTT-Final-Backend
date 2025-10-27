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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;


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
            throw new AppException(ErrorCode.USERNAME_EXSITED, "username");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXSITED, "email");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXSITED, "phone_number");
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public Page<UserResponse> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        if (pageNumber < 1) {
            throw new AppException(ErrorCode.NO_RESULTS);
        }

        Sort sort = sortOrder.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<User> usersList = userRepository.findAll(pageable);
        if (usersList.isEmpty()) {
            throw new AppException(ErrorCode.NO_RESULTS);
        }
        return usersList.map(userMapper::toUserResponse);
    }

    public Page<UserResponse> searchUsers(String query, Pageable pageable) {
        Page<User> usersList = userRepository.findBySearch(query, pageable);
        if (usersList.isEmpty()) {
            throw new AppException(ErrorCode.NO_RESULTS);
        }
        return usersList.map(userMapper::toUserResponse);
    }

    public UserResponse getSelfInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateSelfInfo(UserUpdateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        userMapper.updateUser(request, user);
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public void deleteUser(String id) {
        User user = userRepository.findByUsername(id).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        user.setDeleted(true);
        userRepository.save(user);
    }
}
