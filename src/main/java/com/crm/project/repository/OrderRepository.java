package com.crm.project.repository;

import com.crm.project.entity.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("""
                SELECT DISTINCT o 
                FROM Order o
                LEFT JOIN FETCH o.orderItems
                LEFT JOIN FETCH o.createdBy
                LEFT JOIN FETCH o.lead
                WHERE o.id = :id
            """)
    Optional<Order> findOrderWithRelations(String id);

    @Query("""
                SELECT DISTINCT o 
                FROM Order o
                LEFT JOIN FETCH o.orderItems
                LEFT JOIN FETCH o.createdBy
                LEFT JOIN FETCH o.lead
            """)
    Page<Order> findAllOrdersWithDetails(Pageable pageable);

    boolean existsByOrderCode(String orderCode);

    boolean existsByQuotationId(String quotationId);

    @Query(value = "SELECT status, COUNT(*) AS total FROM orders GROUP BY status", nativeQuery = true)
    List<Map<String, Object>> countByStatus();

    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    BigDecimal sumTotalAmount();

    @Modifying
    @Query("UPDATE Order o SET o.status = 'Cancelled' WHERE o.id = :id")
    void updateStatusToCancelled(@Param("id") String id);
}
