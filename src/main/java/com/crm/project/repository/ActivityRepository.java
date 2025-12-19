package com.crm.project.repository;

import com.crm.project.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {
    @Query("SELECT a FROM Activity a " +
            "JOIN FETCH a.lead l " +
            "WHERE l.user.id = :userId " +
            "ORDER BY a.validUntil ASC")
    List<Activity> findByLeadUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(a) FROM Activity a " +
            "WHERE a.lead.user.id = :userId AND a.completed = :completed")
    Long countByLeadUserIdAndCompleted(
            @Param("userId") String userId,
            @Param("completed") boolean completed
    );

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.lead.user.id = :userId")
    Long countByLeadUserId(@Param("userId") String userId);

    // Chart queries for Tasks Completed
    @Query("""
            SELECT 
                FUNCTION('DAYNAME', a.updatedAt) as dayName,
                CAST(COUNT(a) AS java.math.BigDecimal) as totalCompleted
            FROM Activity a
            WHERE a.completed = true 
            AND a.updatedAt >= :startDate AND a.updatedAt < :endDate
            GROUP BY FUNCTION('DAYOFWEEK', a.updatedAt), FUNCTION('DAYNAME', a.updatedAt)
            ORDER BY FUNCTION('DAYOFWEEK', a.updatedAt)
            """)
    List<Object[]> getCompletedTasksByDayOfWeek(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );

    @Query("""
            SELECT COALESCE(CAST(COUNT(a) AS java.math.BigDecimal), 0)
            FROM Activity a
            WHERE a.completed = true 
            AND a.updatedAt >= :startDate AND a.updatedAt < :endDate
            """)
    BigDecimal getTotalCompletedTasksByPeriod(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );
}
