package com.crm.project.service;

import com.crm.project.dto.request.StageCreationRequest;
import com.crm.project.dto.response.StageResponse;
import com.crm.project.entity.Stage;
import com.crm.project.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StageService {
    private final StageRepository stageRepository;

    public StageResponse createStage(StageCreationRequest request) {
        Stage stage = new Stage();
        stage.setName(request.getName());
        stageRepository.save(stage);
        return StageResponse.builder()
                .id(stage.getId())
                .name(stage.getName())
                .build();
    }


}
