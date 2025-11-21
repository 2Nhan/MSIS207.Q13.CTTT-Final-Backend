package com.crm.project.repository;


import com.crm.project.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends JpaRepository<Lead, String> {

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
