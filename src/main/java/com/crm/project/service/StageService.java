package com.crm.project.service;

import com.crm.project.dto.request.StageCreationRequest;
import com.crm.project.dto.request.StageUpdateRequest;
import com.crm.project.dto.response.StageResponse;
import com.crm.project.dto.response.StagesWithLeadsResponse;
import com.crm.project.entity.Stage;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.mapper.StageMapper;
import com.crm.project.repository.LeadRepository;
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
    private final LeadRepository leadRepository;

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

    @Transactional
    public void deleteStage(String id) {
        if (!stageRepository.existsById(id)) {
            throw new AppException(ErrorCode.STAGE_NOT_FOUND);
        }
        if (leadRepository.existsByStageId(id)) {
            throw new AppException(ErrorCode.STAGE_IN_USE);
        }
        stageRepository.deleteById(id);
    }
}
