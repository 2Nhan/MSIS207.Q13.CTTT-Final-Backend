package com.crm.project.repository;

import com.crm.project.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByUsername(String username);

    @Query(value = """
            SELECT 
                u.*,
            (
                MATCH(u.first_name, u.last_name, u.username, u.email, u.address, u.phone_number)
                AGAINST(:query IN NATURAL LANGUAGE MODE)
                +
                CASE 
                    WHEN LOWER(CONCAT_WS(' ', u.first_name, u.last_name, u.username, u.email, u.address, u.phone_number))
                         LIKE LOWER(CONCAT('%', :query, '%')) THEN 1
                    ELSE 0
                END
                +
                CASE 
                    WHEN LOWER(u.first_name) LIKE LOWER(CONCAT(:query, '%')) THEN 2
                    WHEN LOWER(u.last_name) LIKE LOWER(CONCAT(:query, '%')) THEN 1.5
                    WHEN LOWER(u.username) LIKE LOWER(CONCAT(:query, '%')) THEN 1
                    ELSE 0
                END
            ) AS relevance
            FROM users u
            WHERE (
                MATCH(u.first_name, u.last_name, u.username, u.email, u.address, u.phone_number)
                AGAINST(:query IN NATURAL LANGUAGE MODE)
                OR
                LOWER(CONCAT_WS(' ', u.first_name, u.last_name, u.username, u.email, u.address, u.phone_number))
                LIKE LOWER(CONCAT('%', :query, '%'))
            )
            HAVING relevance > 1
            ORDER BY relevance DESC
            LIMIT 50
            """, nativeQuery = true)
    List<User> findBySearchName(@Param("query") String search);

}
