package com.crm.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCreationRequest {

    private String sku;

    @NotBlank(message = "BLANK_PRODUCT_NAME")
    private String name;

    private String description;

    private String subtitle;

    private String brand;

    private String category;

    private Integer quantity;

    private String status;

    private BigDecimal price;

    private BigDecimal discount;

    private String discountType;

    private String imageUrl;

}
