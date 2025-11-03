package com.crm.project.mapper;

import com.crm.project.dto.representation.ProductCsvRepresentation;
import com.crm.project.dto.request.ProductCreationRequest;
import com.crm.project.dto.response.ProductResponse;
import com.crm.project.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductCreationRequest request);

    ProductResponse toProductResponse(Product product);

    ProductResponse csvToResponse(ProductCsvRepresentation representation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProduct(ProductCreationRequest request, @MappingTarget Product product);
}
