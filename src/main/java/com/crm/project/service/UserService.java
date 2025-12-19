package com.crm.project.service;

import com.crm.project.constant.PredefinedRole;
import com.crm.project.dto.request.UserCreationRequest;
import com.crm.project.dto.request.UserUpdateRequest;
import com.crm.project.dto.response.*;
import com.crm.project.entity.Activity;
import com.crm.project.internal.CloudinaryInfo;
import com.crm.project.entity.User;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.internal.UserStatistics;
import com.crm.project.mapper.UserMapper;
import com.crm.project.repository.ActivityRepository;
import com.crm.project.repository.LeadRepository;
import com.crm.project.repository.RoleRepository;
import com.crm.project.repository.UserRepository;
import com.crm.project.utils.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import com.crm.project.entity.Role;

import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final RoleRepository roleRepository;
    private final LeadRepository leadRepository;
    private final ActivityRepository activityRepository;

    @Transactional
    public ImageResponse uploadAvatar(MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String fileName = FileUploadUtil.standardizeFileName(file.getOriginalFilename());
        FileUploadUtil.checkImage(file, FileUploadUtil.IMAGE_PATTERN);
        CloudinaryInfo cloudinaryResponse = cloudinaryService.uploadFile(file, fileName);
        user.setAvatarUrl(cloudinaryResponse.getUrl());
        userRepository.save(user);
        return ImageResponse.builder().url(cloudinaryResponse.getUrl()).build();
    }

    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXSITED, "username");
        }
        validateUserUniqueness(request.getEmail(), request.getPhoneNumber());
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = roleRepository.findByCode(PredefinedRole.USER_ROLE).orElse(null);
        user.setRole(role);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortOrder) {
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

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> searchUsers(String query, Pageable pageable) {
        Page<User> usersList = userRepository.findBySearch(query, pageable);
        if (usersList.isEmpty()) {
            throw new AppException(ErrorCode.NO_RESULTS);
        }
        return usersList.map(userMapper::toUserResponse);
    }

    public UserResponse getSelfInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse updateSelfInfo(UserUpdateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        validateUserUniqueness(request.getEmail(), request.getPhoneNumber());
        userMapper.updateUser(request, user);
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setDeleted(true);
        userRepository.save(user);
    }

    public List<UserAssignResponse> getUserAssignList() {
        List<User> users = userRepository.findAll();
        List<UserAssignResponse> userAssignResponseList = new ArrayList<>();
        for (User user : users) {
            String id = user.getId();
            String fullName = user.getFirstName() + " " + user.getLastName();
            UserAssignResponse userAssignResponse = new UserAssignResponse();
            userAssignResponse.setId(id);
            userAssignResponse.setFullName(fullName);
            userAssignResponse.setEmail(user.getEmail());
            userAssignResponse.setAvatarUrl(user.getAvatarUrl());
            userAssignResponseList.add(userAssignResponse);
        }
        return userAssignResponseList;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDetailResponse getUserDetails(String userId) {
        // 1. Get user basic info
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Calculate statistics (chỉ count, không fetch leads)
        Long totalLeads = leadRepository.countByUserId(userId);
        Long openLeads = leadRepository.countOpenLeadsByUserId(userId);
        Long convertedLeads = totalLeads - openLeads;

        Long totalActivities = activityRepository.countByLeadUserId(userId);
        Long pendingActivities = activityRepository.countByLeadUserIdAndCompleted(userId, false);
        Long completedActivities = activityRepository.countByLeadUserIdAndCompleted(userId, true);

        UserStatistics statistics = UserStatistics.builder()
                .totalLeads(totalLeads)
                .openLeads(openLeads)
                .convertedLeads(convertedLeads)
                .totalActivities(totalActivities)
                .pendingActivities(pendingActivities)
                .completedActivities(completedActivities)
                .build();

        // 3. Get activities (only pending ones)
        List<Activity> activities = activityRepository.findByLeadUserId(userId);

        // 4. Map activities (chỉ lấy pending, sort by deadline)
        List<ActivitySummaryResponse> activitySummaries = activities.stream()
//                .filter(a -> !a.isCompleted())  // Chỉ lấy chưa hoàn thành
                .sorted(Comparator.comparing(Activity::getValidUntil))
                .map(activity -> ActivitySummaryResponse.builder()
                        .id(activity.getId())
                        .type(activity.getType())
                        .content(activity.getContent())
                        .validUntil(activity.getValidUntil())
                        .status(activity.getStatus())
                        .completed(activity.isCompleted())
                        .createdAt(activity.getCreatedAt())
                        .leadId(activity.getLead().getId())
                        .leadName(activity.getLead().getFullName())
                        .leadCompany(activity.getLead().getCompany())
                        .build())
                .collect(Collectors.toList());

        // 5. Build response (KHÔNG CÓ managedLeads)
        return UserDetailResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .roleName(user.getRole().getCode())
                .statistics(statistics)
                .activities(activitySummaries)  // Chỉ có activities
                .build();
    }

    public UserSummaryResponse getUsersSummary() {
        Long totalUsers = userRepository.countAllUsers();
        Long newThisMonth = userRepository.countNewUsersThisMonth();
        Long activeUsers = userRepository.countActiveUsers(); // Same as totalUsers

        return UserSummaryResponse.builder()
                .totalUsers(totalUsers)
                .newThisMonth(newThisMonth)
                .activeUsers(activeUsers)
                .build();
    }


    private void validateUserUniqueness(String email, String phoneNumber) {
        userRepository.findByEmailOrPhoneNumber(email, phoneNumber).ifPresent(user -> {
            if (email.equals(user.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_EXSITED, "email");
            }
            if (phoneNumber.equals(user.getPhoneNumber())) {
                throw new AppException(ErrorCode.PHONE_NUMBER_EXSITED, "phone_number");
            }
        });
    }
}
