package com.crm.project.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private String id;

    private String sku;

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
