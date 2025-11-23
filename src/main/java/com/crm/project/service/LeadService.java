package com.crm.project.service;

import com.crm.project.dto.request.LeadCreationRequest;
import com.crm.project.internal.CloudinaryInfo;
import com.crm.project.dto.response.LeadResponse;
import com.crm.project.entity.Lead;
import com.crm.project.entity.Stage;
import com.crm.project.entity.User;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.mapper.LeadMapper;
import com.crm.project.repository.LeadRepository;
import com.crm.project.repository.StageRepository;
import com.crm.project.repository.UserRepository;
import com.crm.project.utils.FileUploadUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class LeadService {
    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final StageRepository stageRepository;

    private Stage defaultStage;

    @PostConstruct
    public void init() {
        this.defaultStage = stageRepository.findByName("New").orElseThrow(() -> new AppException(ErrorCode.STAGE_NOT_FOUND));
    }

    public LeadResponse createLead(LeadCreationRequest request, MultipartFile image) {
        validateLeadUniqueness(request.getEmail(), request.getPhoneNumber());
        Lead lead = leadMapper.toLead(request);

        if (image != null && !image.isEmpty()) {
            FileUploadUtil.checkImage(image, FileUploadUtil.IMAGE_PATTERN);
            String filename = FileUploadUtil.standardizeFileName(image.getOriginalFilename());
            CloudinaryInfo cloudinaryResponse = cloudinaryService.uploadFile(image, filename);

            lead.setAvatarUrl(cloudinaryResponse.getUrl());
        }
        lead.setStage(defaultStage);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        lead.setUser(user);

        leadRepository.save(lead);
        return leadMapper.toLeadResponse(lead);
    }

    public LeadResponse getLead(String id) {
        Lead lead = leadRepository.findByIdWithRelations(id).orElseThrow(() -> new AppException(ErrorCode.LEAD_NOT_FOUND));
        return leadMapper.toLeadResponse(lead);
    }

    private void validateLeadUniqueness(String email, String phoneNumber) {
        leadRepository.findByEmailOrPhone(email, phoneNumber).ifPresent(lead -> {
            if (email.equals(lead.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_EXSITED);
            } else if (phoneNumber.equals(lead.getPhoneNumber())) {
                throw new AppException(ErrorCode.PHONE_NUMBER_EXSITED);
            }
        });
    }
}
