package com.crm.project.service;

import com.crm.project.dto.request.StageCreationRequest;
import com.crm.project.dto.request.StageRenameRequest;
import com.crm.project.dto.response.StageResponse;
import com.crm.project.entity.Stage;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.mapper.StageMapper;
import com.crm.project.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;

    public StageResponse createStage(StageCreationRequest request) {
        Stage stage = new Stage();
        stage.setName(request.getName());
        stageRepository.save(stage);
        return StageResponse.builder()
                .id(stage.getId())
                .name(stage.getName())
                .build();
    }

    public StageResponse renameStage(String id, StageRenameRequest request) {
        Stage stage = stageRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.STAGE_NOT_FOUND));
        stage.setName(request.getName());
        stageRepository.save(stage);
        return stageMapper.toStageResponse(stage);
    }
}
