package com.crm.project.repository;

import com.crm.project.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    boolean existsBySkuIn(List<String> skus);

    @Query("SELECT p.sku FROM Product p WHERE p.sku IN :skus")
    Set<String> findBySkuIn(List<String> skus);

    boolean existsBySku(String sku);

    @Query(value = """
            SELECT *
            FROM products
            WHERE deleted = false
            AND (
                  LOWER(name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(tag) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(sku) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            """,
            nativeQuery = true)
    Page<Product> findBySearch(@Param("query") String search, Pageable pageable);
}
