package com.crm.project.mapper;

import com.crm.project.dto.request.LeadCreationRequest;
import com.crm.project.dto.response.LeadResponse;
import com.crm.project.entity.Lead;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeadMapper {
    Lead toLead(LeadCreationRequest request);

    LeadResponse toLeadResponse(Lead lead);
}
