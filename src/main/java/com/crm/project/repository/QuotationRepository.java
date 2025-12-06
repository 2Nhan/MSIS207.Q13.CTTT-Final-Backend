package com.crm.project.repository;

import com.crm.project.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, String> {
    @Query("""
                SELECT q FROM Quotation q
                LEFT JOIN FETCH q.lead
                LEFT JOIN FETCH q.items i
                LEFT JOIN FETCH i.product
                WHERE q.id = :id
            """)
    Optional<Quotation> findQuotationDetailById(String id);


    @Modifying
    @Query("UPDATE Quotation q SET q.status = 'SENT' WHERE q.id = :id")
    void updateStatusToSent(@Param("id") String id);
}
