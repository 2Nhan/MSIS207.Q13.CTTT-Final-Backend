package com.crm.project.dto.request;

import com.crm.project.validator.custom_validator.BlankUpdateConstraint;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdateRequest {
    @BlankUpdateConstraint(message = "BLANK_SKU")
    private String sku;

    @BlankUpdateConstraint(message = "BLANK_PRODUCT_NAME")
    private String name;

    private String description;

    private String subtitle;

    private String brand;

    @BlankUpdateConstraint(message = "BLANK_CATEGORY")
    private String category;

    private Integer quantity;

    @BlankUpdateConstraint(message = "BLANK_STATUS")
    private String status;

    //    @BlankUpdateConstraint(message = "BLANK_PRICE")
    private BigDecimal price;

    private BigDecimal discount;

    private String discountType;

    private String tag;
}
