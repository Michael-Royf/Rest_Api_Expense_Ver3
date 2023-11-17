package com.michael.expense.repository;

import com.michael.expense.entity.EmailConfirmationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long> {

    Optional<EmailConfirmationToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE EmailConfirmationToken c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.token = ?1")
    int updateConfirmedDate(String token, LocalDateTime confirmedDate);
}
