package com.crm.project.service;

import com.crm.project.dto.request.OrderCreationFromQuotationRequest;
import com.crm.project.dto.response.OrderResponse;
import com.crm.project.entity.Order;
import com.crm.project.entity.OrderItem;
import com.crm.project.entity.Quotation;
import com.crm.project.entity.User;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.mapper.OrderMapper;
import com.crm.project.repository.OrderRepository;
import com.crm.project.repository.QuotationRepository;
import com.crm.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final QuotationRepository quotationRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrderFromQuotation(String quotationId, OrderCreationFromQuotationRequest request) {
        Quotation quotation = quotationRepository.findQuotationDetailById(quotationId).orElseThrow(() -> new AppException(ErrorCode.QUOTATION_NOT_FOUND));

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<OrderItem> orderItems = quotation.getItems().stream().map(orderMapper::fromQuotationItemToOrderItem).toList();

        Order order = Order.builder()
                .orderCode(request.getOrderCode())
                .shippingAddress(request.getShippingAddress())
                .totalAmount(quotation.getFinalTotal())
                .status("Pending")
                .quotation(quotation)
                .orderItems(orderItems)
                .lead(quotation.getLead())
                .createdBy(user)
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        order.setOrderItems(orderItems);

        orderRepository.save(order);

        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        orderResponse.setBuyerName(order.getLead().getFullName());
        orderResponse.setItems(orderItems.stream().map(orderMapper::fromOrderItemToOrderItemInfo).toList());
        return orderResponse;
    }

    public OrderResponse getOrderDetails(String id) {
        Order order = orderRepository.findOrderWithRelations(id).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        return orderResponse;
    }

    public Page<OrderResponse> getAllOrders(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Order> orders = orderRepository.findAllOrdersWithDetails(pageable);
        return orders.map(orderMapper::toOrderResponse);
    }

    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        orderRepository.deleteById(id);
    }
}
