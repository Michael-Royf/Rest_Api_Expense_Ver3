package com.michael.expense.repository;

import com.michael.expense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.isActive = TRUE WHERE a.email = ?1")
    int enableUser(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.isActive = FALSE WHERE a.email = ?1")
    int disabledUser(String email);
}
