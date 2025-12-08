package com.crm.project.service;

import com.crm.project.dto.request.LeadCreationRequest;
import com.crm.project.dto.request.LeadUpdateRequest;
import com.crm.project.dto.request.MatchingRequest;
import com.crm.project.dto.response.ImportResultResponse;
import com.crm.project.dto.response.StagesWithLeadsResponse;
import com.crm.project.internal.CloudinaryInfo;
import com.crm.project.dto.response.LeadResponse;
import com.crm.project.entity.Lead;
import com.crm.project.entity.Stage;
import com.crm.project.entity.User;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.internal.ImportInfo;
import com.crm.project.mapper.LeadMapper;
import com.crm.project.mapper.StageMapper;
import com.crm.project.repository.LeadRepository;
import com.crm.project.repository.StageRepository;
import com.crm.project.repository.UserRepository;
import com.crm.project.utils.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LeadService {
    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final CsvService csvService;

    @Transactional
    public LeadResponse createLead(String stageId, LeadCreationRequest request) {
        validateLeadUniqueness(request.getEmail(), request.getPhoneNumber());
        Lead lead = leadMapper.toLead(request);
        MultipartFile image = request.getImage();
        if (image != null && !image.isEmpty()) {
            FileUploadUtil.checkImage(image, FileUploadUtil.IMAGE_PATTERN);
            String filename = FileUploadUtil.standardizeFileName(image.getOriginalFilename());
            CloudinaryInfo cloudinaryResponse = cloudinaryService.uploadFile(image, filename);

            lead.setAvatarUrl(cloudinaryResponse.getUrl());
        }
        Stage stage = stageRepository.findById(stageId).orElseThrow(() -> new AppException(ErrorCode.STAGE_NOT_FOUND));
        lead.setStage(stage);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        lead.setUser(user);

        leadRepository.save(lead);
        return leadMapper.toLeadResponse(lead);
    }

    public LeadResponse getLead(String id) {
        Lead lead = leadRepository.findByIdWithRelations(id).orElseThrow(() -> new AppException(ErrorCode.LEAD_NOT_FOUND));
        return leadMapper.toLeadResponse(lead);
    }

    public List<StagesWithLeadsResponse> getStagesWithLeads() {
        List<Stage> stages = stageRepository.findAllWithLeads();
        return stages.stream().map(stageMapper::toStagesWithLeadsResponse).toList();
    }

    @Transactional
    public LeadResponse updateLead(String id, LeadUpdateRequest request) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.LEAD_NOT_FOUND));
        MultipartFile image = request.getImage();
        leadMapper.updateLead(request, lead);
        if (image != null && !image.isEmpty()) {
            FileUploadUtil.checkImage(image, FileUploadUtil.IMAGE_PATTERN);
            String filename = FileUploadUtil.standardizeFileName(image.getOriginalFilename());
            CloudinaryInfo cloudinaryResponse = cloudinaryService.uploadFile(image, filename);
            lead.setAvatarUrl(cloudinaryResponse.getUrl());
        }
        leadRepository.save(lead);
        return leadMapper.toLeadResponse(lead);
    }

    @Transactional
    public void updateLeadAssignment(String id, String userId) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.LEAD_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        lead.setUser(user);
        leadRepository.save(lead);
    }

    @Transactional
    public void updateLeadStage(String id, String stageId) {
        Stage stage = stageRepository.findById(stageId).orElseThrow(() -> new AppException(ErrorCode.STAGE_NOT_FOUND));

        if (!leadRepository.existsById(id)) {
            throw new AppException(ErrorCode.LEAD_NOT_FOUND);
        }

        if (stage.getName().equalsIgnoreCase("Won")) {
            leadRepository.updateStatus(id);
        }

        leadRepository.updateStage(id, stageId);
    }

    public List<StagesWithLeadsResponse> searchLeads(String query) {
        List<Stage> result = stageRepository.searchLeadsGroupedByStage(query);
        return result.stream().map(stageMapper::toStagesWithLeadsResponse).toList();
    }

    @Transactional
    public void deleteLead(String id) {
        if (!leadRepository.existsById(id)) {
            throw new AppException(ErrorCode.LEAD_NOT_FOUND);
        }
        leadRepository.deleteById(id);
    }

//    @Transactional
//    public ImportResultResponse<LeadResponse> importLeadsFromCsc(
//            MatchingRequest matching,
//            MultipartFile file
//    ) throws IOException {
//        ImportInfo importInfo = csvService.parseCsvFile(matching.getMatching(), file);
//
//        List<Lead> leads = importInfo.getData()
//                .stream()
//                .map(leadMapper::importToLead)
//                .toList();
//
//        List<Lead> invalidList = new ArrayList<>();
//        List<Lead> tempList = new ArrayList<>();
//        List<Lead> validList = new ArrayList<>();
//    }

    private void validateLeadUniqueness(String email, String phoneNumber) {
        leadRepository.findByEmailOrPhone(email, phoneNumber).ifPresent(lead -> {
            if (email.equals(lead.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_EXSITED);
            } else if (phoneNumber.equals(lead.getPhoneNumber())) {
                throw new AppException(ErrorCode.PHONE_NUMBER_EXSITED);
            }
        });
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
