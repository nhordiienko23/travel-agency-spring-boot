package com.epam.finaltask.repository;

import com.epam.finaltask.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByUsername(String username);

    @Query("SELECT u FROM User u WHERE " +
            ":keyword IS NULL OR :keyword = '' OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(CAST(u.role AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "(LOWER(:keyword) = 'active' AND u.active = true) OR " +
            "(LOWER(:keyword) = 'blocked' AND u.active = false)")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}