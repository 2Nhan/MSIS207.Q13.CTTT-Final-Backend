package com.crm.project.dto.response;

import com.crm.project.internal.OrderItemInfo;
import com.crm.project.internal.UserNormalInfo;
import lombok.*;

import java.util.List;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private String id;

    private String orderCode;

    private String buyerName;

    private String shippingAddress;

    private String status;

    private BigDecimal totalAmount;

    private List<OrderItemInfo> items;

    private UserNormalInfo createdBy;
}
