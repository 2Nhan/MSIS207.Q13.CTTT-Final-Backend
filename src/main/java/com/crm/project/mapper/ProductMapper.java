package com.crm.project.mapper;

import com.crm.project.dto.request.ProductCreationRequest;
import com.crm.project.dto.request.ProductUpdateRequest;
import com.crm.project.dto.response.ProductResponse;
import com.crm.project.entity.Product;

import java.math.BigDecimal;
import java.util.Map;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductCreationRequest request);

    ProductResponse toProductResponse(Product product);

    @Mapping(target = "quantity", expression = "java(toInteger(data.get(\"quantity\")))")
    @Mapping(target = "price", expression = "java(toBigDecimal(data.get(\"price\")))")
    @Mapping(target = "discount", expression = "java(toBigDecimal(data.get(\"discount\")))")
    @Mapping(target = "id", ignore = true) // nếu bạn không import id từ CSV
    @Mapping(target = "orderItems", ignore = true)
    Product importToProduct(Map<String, String> data);

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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProduct(ProductUpdateRequest request, @MappingTarget Product product);
}
