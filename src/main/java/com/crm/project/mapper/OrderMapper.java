package com.crm.project.mapper;

import com.crm.project.dto.response.OrderResponse;
import com.crm.project.entity.Order;
import com.crm.project.entity.OrderItem;
import com.crm.project.entity.QuotationItem;
import com.crm.project.internal.OrderItemInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", source = "product")
    OrderItem fromQuotationItemToOrderItem(QuotationItem quotationItem);

    @Mapping(target = "buyerName", source = "order.lead.fullName")
    @Mapping(target = "items", source = "order.orderItems")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    OrderItemInfo fromOrderItemToOrderItemInfo(OrderItem orderItem);
}
