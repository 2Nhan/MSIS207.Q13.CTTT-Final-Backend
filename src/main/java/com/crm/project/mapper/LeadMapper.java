package com.crm.project.mapper;

import com.crm.project.dto.request.LeadCreationRequest;
import com.crm.project.dto.request.LeadUpdateRequest;
import com.crm.project.dto.response.CustomerResponse;
import com.crm.project.dto.response.LeadResponse;
import com.crm.project.entity.Lead;
import com.crm.project.internal.LeadQuotationInfo;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface LeadMapper {
    @Mapping(target = "avatarUrl", ignore = true)
    Lead toLead(LeadCreationRequest request);

    LeadResponse toLeadResponse(Lead lead);

    LeadQuotationInfo toLeadQuotationInfo(Lead lead);

    @Mapping(target = "stage", ignore = true)
    @Mapping(target = "quotations", ignore = true)
    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLead(LeadUpdateRequest request, @MappingTarget Lead lead);

    CustomerResponse toCustomerResponse(Lead lead);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "quotations", ignore = true)
    @Mapping(target = "activities", ignore = true)
    @Mapping(target = "stage", ignore = true)
    Lead importToLead(Map<String, String> data);

    // === Converters ===
    default Integer toInteger(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    default BigDecimal toBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    default LocalDate toLocalDate(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            // Hỗ trợ 2 định dạng phổ biến trong CSV
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            try {
                return LocalDate.parse(value.trim(), formatter1);
            } catch (Exception e) {
                return LocalDate.parse(value.trim(), formatter2);
            }
        } catch (Exception e) {
            return null;
        }
    }

    default String toStringValue(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }

    // --- Custom field mapping ---
    @AfterMapping
    default void mapFields(@MappingTarget Lead.LeadBuilder lead, Map<String, String> data) {
        lead.fullName(toStringValue(data.get("fullName")));
        lead.dateOfBirth(toLocalDate(data.get("dateOfBirth")));
        lead.email(toStringValue(data.get("email")));
        lead.phoneNumber(toStringValue(data.get("phoneNumber")));
        lead.company(toStringValue(data.get("company")));
        lead.rating(toInteger(data.get("rating")));
        lead.expectedRevenue(toBigDecimal(data.get("expectedRevenue")));
        lead.note(toStringValue(data.get("note")));
        lead.source(toStringValue(data.get("source")));
        lead.avatarUrl(toStringValue(data.get("avatarUrl")));
    }
}
