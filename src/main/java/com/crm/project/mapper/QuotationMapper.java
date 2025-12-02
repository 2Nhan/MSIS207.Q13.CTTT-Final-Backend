package com.crm.project.mapper;


import com.crm.project.entity.QuotationItem;
import com.crm.project.internal.QuotationItemInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuotationMapper {
    public QuotationItem toQuotationItem(QuotationItemInfo quotationItemInfo);
}
