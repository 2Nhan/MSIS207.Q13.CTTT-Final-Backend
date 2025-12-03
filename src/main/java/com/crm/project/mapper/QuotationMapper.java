package com.crm.project.mapper;


import com.crm.project.dto.response.QuotationResponse;
import com.crm.project.entity.Quotation;
import com.crm.project.entity.QuotationItem;
import com.crm.project.internal.QuotationItemInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuotationMapper {
    public QuotationItem toQuotationItem(QuotationItemInfo quotationItemInfo);

    @Mapping(target = "productId", source = "product.id")
    QuotationItemInfo quotationItemToQuotationItemInfo(QuotationItem quotationItem);

    public QuotationResponse toQuotationResponse(Quotation quotation);
}
