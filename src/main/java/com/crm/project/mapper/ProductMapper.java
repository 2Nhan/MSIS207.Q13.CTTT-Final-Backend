package com.crm.project.mapper;

import com.crm.project.dto.request.ProductCreationRequest;
import com.crm.project.dto.response.ProductResponse;
import com.crm.project.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductCreationRequest request);

    ProductResponse toProductResponse(Product product);
}
