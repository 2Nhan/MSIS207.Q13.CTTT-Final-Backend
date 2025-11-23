package com.crm.project.repository;


import com.crm.project.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, String> {
    @Query("SELECT l FROM Lead l WHERE l.email = :email OR l.phoneNumber = :phone")
    Optional<Lead> findByEmailOrPhone(@Param("email") String email, @Param("phone") String phone);

    @Query("""
                SELECT l FROM Lead l
                LEFT JOIN FETCH l.stage
                LEFT JOIN FETCH l.user
                WHERE l.id = :id
            """)
    Optional<Lead> findByIdWithRelations(@Param("id") String id);

    @Modifying
    @Query("UPDATE Lead l SET l.stage.id = :stageId WHERE l.id = :leadId")
    int updateStage(@Param("leadId") String leadId, @Param("stageId") String stageId);

}
