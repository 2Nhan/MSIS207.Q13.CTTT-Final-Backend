package com.crm.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String sku;

    private String name;

    private String description;

    private int quantity;

    private BigDecimal price;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;
}
