package com.crm.project.mapper;

import com.crm.project.dto.request.LeadCreationRequest;
import com.crm.project.dto.response.LeadResponse;
import com.crm.project.entity.Lead;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LeadMapper {
    @Mapping(target = "avatarUrl", ignore = true)
    Lead toLead(LeadCreationRequest request);

    LeadResponse toLeadResponse(Lead lead);
}
