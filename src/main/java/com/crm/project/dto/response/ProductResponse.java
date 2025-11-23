package com.crm.project.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({
        "productId",
        "sku",
        "productName",
        "description",
        "productSubtitle",
        "productBrand",
        "productCategory",
        "quantity",
        "status",
        "purchaseUnitPrice",
        "discount",
        "discountType",
        "imageUrl"
})
public class ProductResponse {
    @JsonProperty("productId")
    private String id;

    private String sku;

    @JsonProperty("productName")
    private String name;

    private String description;

    @JsonProperty("productSubtitle")
    private String subtitle;

    @JsonProperty("productBrand")
    private String brand;

    @JsonProperty("productCategory")
    private String category;

    private Integer quantity;

    private String status;

    @JsonProperty("purchaseUnitPrice")
    private BigDecimal price;

    private BigDecimal discount;

    private String discountType;

    private String tag;

    private String imageUrl;
}
