package com.crm.project.mapper;

import com.crm.project.dto.response.StageResponse;
import com.crm.project.dto.response.StagesWithLeadsResponse;
import com.crm.project.entity.Stage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StageMapper {
    StageResponse toStageResponse(Stage stage);

    StagesWithLeadsResponse toStagesWithLeadsResponse(Stage stage);
}
