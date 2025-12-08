package com.crm.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreationFromQuotationRequest {

    @NotBlank(message = "BLANK_ORDER_CODE")
    private String orderCode;

    @NotBlank(message = "BLANK_SHIPPING_ADDRESS")
    private String shippingAddress;
}
