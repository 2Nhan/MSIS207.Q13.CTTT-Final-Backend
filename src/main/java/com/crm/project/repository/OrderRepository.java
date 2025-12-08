package com.crm.project.repository;

import com.crm.project.entity.Order;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
