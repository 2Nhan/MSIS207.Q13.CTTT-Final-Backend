package com.crm.project.repository;


import com.crm.project.entity.Lead;
import com.crm.project.entity.Stage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, String> {
    @Query("SELECT l FROM Lead l WHERE l.email = :email OR l.phoneNumber = :phone")
    Optional<Lead> findByEmailOrPhone(@Param("email") String email, @Param("phone") String phone);

    @Query("""
            SELECT l FROM Lead l
            LEFT JOIN FETCH l.stage
            LEFT JOIN FETCH l.user
            LEFT JOIN FETCH l.activities
            WHERE l.id = :id
            """)
    Optional<Lead> findByIdWithRelations(@Param("id") String id);

    boolean existsByStageId(String stageId);

    @Modifying
    @Query("UPDATE Lead l SET l.stage.id = :stageId WHERE l.id = :leadId")
    int updateStage(@Param("leadId") String leadId, @Param("stageId") String stageId);

    @Modifying
    @Query("UPDATE Lead l SET l.status = 'CONVERTED' WHERE l.id = :leadId")
    int updateStatus(@Param("leadId") String leadId);

    @Query("""
                    SELECT l FROM Lead l
                    LEFT JOIN FETCH l.user
                    WHERE l.id = :id AND l.status = 'CONVERTED'
            """)
    Optional<Lead> getCustomerDetails(@Param("id") String id);

    @Query("""
                SELECT l FROM Lead l
                JOIN l.user u
                WHERE l.status = 'CONVERTED'
            """)
    Page<Lead> findAllCustomer(Pageable pageable);

    @Query("""
                SELECT l FROM Lead l
                JOIN l.user u
                WHERE l.status = 'CONVERTED'
                            AND LOWER(l.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    Page<Lead> findCustomersBySearch(String query, Pageable pageable);

    @Query("""
                SELECT l 
                FROM Lead l
                WHERE 
                    (l.email IS NOT NULL AND l.email IN :emails)
                    OR (l.phoneNumber IS NOT NULL AND l.phoneNumber IN :phones)
            """)
    List<Lead> findExistingLeadsByEmailOrPhone(@Param("emails") List<String> emails,
                                               @Param("phones") List<String> phones);


    @Query("SELECT COUNT(l) FROM Lead l WHERE l.user.id = :userId")
    Long countByUserId(@Param("userId") String userId);


    @Query("SELECT COUNT(l) FROM Lead l WHERE l.user.id = :userId " +
            "AND (l.status IS NULL OR l.status != 'CONVERTED')")
    Long countOpenLeadsByUserId(@Param("userId") String userId);

    // Chart queries for Lead Conversion
    @Query("""
            SELECT 
                FUNCTION('DAYNAME', l.updatedAt) as dayName,
                CAST(COUNT(l) AS java.math.BigDecimal) as totalConverted
            FROM Lead l
            WHERE l.status = 'CONVERTED'
            AND l.updatedAt >= :startDate AND l.updatedAt < :endDate
            GROUP BY FUNCTION('DAYOFWEEK', l.updatedAt), FUNCTION('DAYNAME', l.updatedAt)
            ORDER BY FUNCTION('DAYOFWEEK', l.updatedAt)
            """)
    List<Object[]> getConvertedLeadsByDayOfWeek(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );

    @Query("""
            SELECT COALESCE(CAST(COUNT(l) AS java.math.BigDecimal), 0)
            FROM Lead l
            WHERE l.status = 'CONVERTED'
            AND l.updatedAt >= :startDate AND l.updatedAt < :endDate
            """)
    BigDecimal getTotalConvertedLeadsByPeriod(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );

    // Count total leads
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.status = 'CONVERTED'")
    Long countTotalLeadsConversion();
}
