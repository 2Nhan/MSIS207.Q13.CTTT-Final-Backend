package com.crm.project.repository;


import com.crm.project.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, String> {
    @Query("SELECT l FROM Lead l WHERE l.email = :email OR l.phoneNumber = :phone")
    Optional<Lead> findByEmailOrPhone(@Param("email") String email, @Param("phone") String phone);

}
