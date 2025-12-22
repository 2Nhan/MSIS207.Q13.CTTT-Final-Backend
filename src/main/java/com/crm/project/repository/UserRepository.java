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

    @Query("""
            SELECT u
            FROM User u
            JOIN FETCH u.role
            WHERE u.username = :username
            """)
    Optional<User> findByUsernameWithRole(String username);

    @Query(value = """
            SELECT *
            FROM users u
            WHERE (
                  LOWER(first_name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(last_name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(username) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(email) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(address) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(phone_number) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(CONCAT(first_name, ' ', last_name)) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(CONCAT(last_name, ' ', first_name)) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            """,
            nativeQuery = true)
    Page<User> findBySearch(@Param("query") String search, Pageable pageable);

    // Count active users (deleted = false)
    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false")
    Long countActiveUsers();


    @Query("SELECT COUNT(u) FROM User u")
    Long countAllUsers();

    // Count users created in current month
    @Query("""
            SELECT COUNT(u) FROM User u 
            WHERE u.deleted = false 
            AND FUNCTION('YEAR', u.createdAt) = FUNCTION('YEAR', CURRENT_DATE)
            AND FUNCTION('MONTH', u.createdAt) = FUNCTION('MONTH', CURRENT_DATE)
            """)
    Long countNewUsersThisMonth();
}
