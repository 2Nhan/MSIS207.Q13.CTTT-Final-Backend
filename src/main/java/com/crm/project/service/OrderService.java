package com.crm.project.service;

import com.crm.project.dto.request.OrderCreationFromQuotationRequest;
import com.crm.project.dto.response.OrderResponse;
import com.crm.project.entity.*;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.mapper.OrderMapper;
import com.crm.project.repository.LeadRepository;
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

import java.util.HashMap;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final QuotationRepository quotationRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final LeadRepository leadRepository;

    @Transactional
    public OrderResponse createOrderFromQuotation(String quotationId, OrderCreationFromQuotationRequest request) {
        Quotation quotation = quotationRepository.findQuotationDetailById(quotationId).orElseThrow(() -> new AppException(ErrorCode.QUOTATION_NOT_FOUND));

        if (orderRepository.existsByQuotationId(quotationId)) {
            throw new AppException(ErrorCode.QUOTATION_ORDER_EXISTED);
        }

        if (orderRepository.existsByOrderCode(request.getOrderCode())) {
            throw new AppException(ErrorCode.ORDER_CODE_EXISTED);
        }

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

        quotation.setStatus("Ordered");
        quotationRepository.save(quotation);

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

    @Transactional
    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        orderRepository.deleteById(id);
    }

    @Transactional
    public OrderResponse completeOrder(String id) {
        Order order = orderRepository.findOrderWithRelations(id).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        Lead lead = order.getLead();
        leadRepository.updateStatus(lead.getId());
        order.setStatus("Delivered");
        order.setLead(lead);
        orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }

    public Map<String, Long> getOrdersSummary() {
        List<Map<String, Object>> count = orderRepository.countByStatus();
        Map<String, Long> result = new HashMap<>();
        for (Map<String, Object> map : count) {
            result.put((String) map.get("status"), (long) map.get("total"));
        }
        BigDecimal totalAmount = orderRepository.sumTotalAmount();
        result.put("totalAmount", totalAmount.toBigInteger().longValue());
        return result;
    }

    @Transactional
    public OrderResponse cancelOrder(String id) {
        Order order = orderRepository.findOrderWithRelations(id).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus("Cancelled");
        orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }

    public Page<OrderResponse> searchOrders(String query, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Order> orders = orderRepository.findOrdersBySearch(query, pageable);
        if (orders.isEmpty()) {
            throw new AppException(ErrorCode.NO_RESULTS);
        }
        return orders.map(orderMapper::toOrderResponse);
    }
}
