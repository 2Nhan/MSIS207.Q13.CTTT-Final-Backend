package com.crm.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCreationRequest {
    @NotBlank(message = "BLANK_SKU")
    private String sku;

    @NotBlank(message = "BLANK_PRODUCT_NAME")
    private String name;

    private String description;

    private String subtitle;

    private String brand;

    @NotBlank(message = "BLANK_CATEGORY")
    private String category;

    private Integer quantity;

    @NotBlank(message = "BLANK_STATUS")
    private String status;

    @NotNull(message = "BLANK_PRICE")
    private BigDecimal price;

    private BigDecimal discount;

    private String discountType;

    private String tag;

    private MultipartFile image;

}
