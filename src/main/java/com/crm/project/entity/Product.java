package com.crm.project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.List;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
@Where(clause = "deleted = false")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String subtitle;

    private String brand;

    private String category;

    private Integer quantity;

    private String status;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal discount;

    @Column(name = "discount_type")
    private String discountType;

    private String tag;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "deleted")
    private boolean deleted;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    @ManyToMany(mappedBy = "products")
    private List<Quotation> quotations;

}
