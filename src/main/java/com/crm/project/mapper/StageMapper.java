package com.crm.project.mapper;

import com.crm.project.dto.request.StageUpdateRequest;
import com.crm.project.dto.response.StageResponse;
import com.crm.project.dto.response.StagesWithLeadsResponse;
import com.crm.project.entity.Stage;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface StageMapper {
    StageResponse toStageResponse(Stage stage);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStage(StageUpdateRequest request, @MappingTarget Stage stage);

    StagesWithLeadsResponse toStagesWithLeadsResponse(Stage stage);
}
