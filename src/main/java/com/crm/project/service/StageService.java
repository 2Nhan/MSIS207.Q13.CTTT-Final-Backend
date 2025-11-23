package com.crm.project.service;

import com.crm.project.dto.request.StageCreationRequest;
import com.crm.project.dto.request.StageUpdateRequest;
import com.crm.project.dto.response.StageResponse;
import com.crm.project.dto.response.StagesWithLeadsResponse;
import com.crm.project.entity.Stage;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.mapper.StageMapper;
import com.crm.project.repository.StageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;

    @Transactional
    public StageResponse createStage(StageCreationRequest request) {
        Stage stage = new Stage();
        stage.setName(request.getName());
        stageRepository.save(stage);
        return StageResponse.builder()
                .id(stage.getId())
                .name(stage.getName())
                .build();
    }

    @Transactional
    public StageResponse renameStage(String id, StageUpdateRequest request) {
        Stage stage = stageRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.STAGE_NOT_FOUND));
        stage.setName(request.getName());
        stageRepository.save(stage);
        return stageMapper.toStageResponse(stage);
    }

    public List<StagesWithLeadsResponse> getStagesWithLeads() {
        List<Stage> stages = stageRepository.findAllWithLeads();
        return stages.stream().map(stageMapper::toStagesWithLeadsResponse).toList();
    }
}
