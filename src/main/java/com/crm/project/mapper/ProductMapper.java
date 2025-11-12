package com.crm.project.mapper;

import com.crm.project.dto.request.ProductCreationRequest;
import com.crm.project.dto.request.ProductUpdateRequest;
import com.crm.project.dto.response.ProductResponse;
import com.crm.project.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductCreationRequest request);

    ProductResponse toProductResponse(Product product);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProduct(ProductUpdateRequest request, @MappingTarget Product product);
}
