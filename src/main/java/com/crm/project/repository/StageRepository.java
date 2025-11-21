package com.crm.project.repository;

import com.crm.project.entity.Stage;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StageRepository extends JpaRepository<Stage, String> {

    Optional<Stage> findByName(String name);
}
