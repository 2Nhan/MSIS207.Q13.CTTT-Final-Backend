package com.crm.project.repository;

import com.crm.project.entity.Stage;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageRepository extends JpaRepository<Stage, String> {

    Optional<Stage> findByName(String name);

    @Query("SELECT DISTINCT s FROM Stage s LEFT JOIN FETCH s.leads")
    List<Stage> findAllWithLeads();
}
