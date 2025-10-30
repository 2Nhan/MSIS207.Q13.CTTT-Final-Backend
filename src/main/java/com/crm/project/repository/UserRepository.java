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

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByUsername(String username);

    @Query(value = """
            SELECT * FROM users
            WHERE 
                (MATCH(first_name, last_name, username, email, address, phone_number)
                AGAINST (:query IN NATURAL LANGUAGE MODE)
                OR
                LOWER(CONCAT(first_name, ' ', last_name))
                LIKE LOWER(CONCAT('%', :query, '%')))
                AND deleted = false
            """, nativeQuery = true)
    Page<User> findBySearch(@Param("query") String search, Pageable pageable);

}
