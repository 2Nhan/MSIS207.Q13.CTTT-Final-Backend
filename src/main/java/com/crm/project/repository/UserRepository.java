package com.crm.project.repository;

import com.crm.project.entity.User;

import java.util.Optional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.email = :email OR u.phoneNumber = :phone")
    Optional<User> findByEmailOrPhoneNumber(String email, String phone);

    Optional<User> findByUsername(String username);

    @Query(value = """
            SELECT *
            FROM users
            AND (
                  LOWER(first_name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(last_name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(username) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(email) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(address) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(phone_number) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(CONCAT(first_name, ' ', last_name)) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            """,
            nativeQuery = true)
    Page<User> findBySearch(@Param("query") String search, Pageable pageable);

}
