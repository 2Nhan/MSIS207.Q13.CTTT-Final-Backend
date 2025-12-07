package com.crm.project.service;

import com.crm.project.dto.request.ActivityCreationRequest;
import com.crm.project.dto.response.ActivityResponse;
import com.crm.project.entity.Activity;
import com.crm.project.entity.Lead;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.mapper.ActivityMapper;
import com.crm.project.repository.ActivityRepository;
import com.crm.project.repository.LeadRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final LeadRepository leadRepository;
    private final ActivityMapper activityMapper;

    @Transactional
    public ActivityResponse createActivity(String id, ActivityCreationRequest request) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.LEAD_NOT_FOUND));

        Activity activity = activityMapper.toActivity(request);
        activity.setLead(lead);
        activity.setStatus("PENDING");
        activityRepository.save(activity);

        return activityMapper.toActivityResponse(activity);
    }

    @Transactional
    public ActivityResponse markActivityAsCompleted(String leadId, String activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new AppException(ErrorCode.ACTIVITY_NOT_FOUND));

        if (!activity.getLead().getId().equals(leadId)) {
            throw new AppException(ErrorCode.INVALID_LEAD_ACTIVITY_RELATION);
        }

        activity.setCompleted(true);
        activity.setStatus("DONE");

        activityRepository.save(activity);

        return activityMapper.toActivityResponse(activity);
    }

    @Transactional
    public void deleteActivity(String leadId, String activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new AppException(ErrorCode.ACTIVITY_NOT_FOUND));

        if (!activity.getLead().getId().equals(leadId)) {
            throw new AppException(ErrorCode.INVALID_LEAD_ACTIVITY_RELATION);
        }

        activityRepository.delete(activity);
    }
}
