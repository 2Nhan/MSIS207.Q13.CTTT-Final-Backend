package com.crm.project.mapper;

import com.crm.project.dto.request.ActivityCreationRequest;
import com.crm.project.dto.response.ActivityResponse;
import com.crm.project.entity.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    @Mapping(target = "lead", ignore = true)
    Activity toActivity(ActivityCreationRequest request);

    ActivityResponse toActivityResponse(Activity activity);
}
