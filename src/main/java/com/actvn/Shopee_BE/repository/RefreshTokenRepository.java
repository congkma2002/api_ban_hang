package com.actvn.Shopee_BE.repository;

import com.actvn.Shopee_BE.entity.RefreshToken;
import com.actvn.Shopee_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String Token);

    @Modifying
    void deleteByUser(User user);

    RefreshToken findByUser(User user);
}
