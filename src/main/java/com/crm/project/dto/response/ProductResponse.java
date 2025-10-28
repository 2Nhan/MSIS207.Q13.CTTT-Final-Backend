package com.crm.project.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private int quantity;

    private BigDecimal price;

    @JsonProperty("image_url")
    private String imageUrl;

    private String unit;
}
