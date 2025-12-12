package com.crm.project.repository;

import com.crm.project.entity.Quotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.List;
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

    @Query("""
            SELECT DISTINCT q FROM Quotation q
            LEFT JOIN FETCH q.lead
            LEFT JOIN FETCH q.items i
            LEFT JOIN FETCH i.product
            """)
    Page<Quotation> findAllQuotationsWithDetails(Pageable pageable);


    @Modifying
    @Query("UPDATE Quotation q SET q.status = 'Sent' WHERE q.id = :id")
    void updateStatusToSent(@Param("id") String id);

    @Modifying
    @Query("UPDATE Quotation q SET q.fileUrl = :filePath WHERE q.id = :id")
    void updateFilePath(@Param("id") String id, @Param("filePath") String filePath);

    @Modifying
    @Query("""
                UPDATE Quotation q 
                SET q.status = 'Expired' 
                WHERE q.validUntil < CURRENT_DATE 
                  AND q.status = 'Draft'
            """)
    int markExpiredQuotations();
    
}
