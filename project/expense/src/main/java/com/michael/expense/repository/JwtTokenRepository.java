package com.michael.expense.repository;

import com.michael.expense.entity.JWTToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JwtTokenRepository extends JpaRepository<JWTToken, Long> {

    @Query("SELECT t from JWTToken t join User u on t.user.id =u.id where  u.id =:userId and (t.expired =false or t.revoked =false)")
    List<JWTToken> findAllValidTokensByUser(Long userId);

    Optional<JWTToken> findByToken(String token);
}
