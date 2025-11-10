package com.crm.project.repository;

import com.crm.project.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsBySkuIn(List<String> skus);

    boolean existsBySku(String sku);
}
