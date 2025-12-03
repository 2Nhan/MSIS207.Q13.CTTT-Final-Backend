package com.crm.project.mapper;

import com.crm.project.dto.request.LeadCreationRequest;
import com.crm.project.dto.request.LeadUpdateRequest;
import com.crm.project.dto.response.LeadResponse;
import com.crm.project.entity.Lead;
import com.crm.project.internal.LeadQuotationInfo;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LeadMapper {
    @Mapping(target = "avatarUrl", ignore = true)
    Lead toLead(LeadCreationRequest request);

    LeadResponse toLeadResponse(Lead lead);

    LeadQuotationInfo toLeadQuotationInfo(Lead lead);

    @Mapping(target = "stage", ignore = true)
    @Mapping(target = "quotations", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLead(LeadUpdateRequest request, @MappingTarget Lead lead);
}
