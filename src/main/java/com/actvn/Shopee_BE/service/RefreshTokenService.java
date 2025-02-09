package com.actvn.Shopee_BE.service;

import com.actvn.Shopee_BE.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(String userId);

    void deleteByUserId(String userId);

    RefreshToken verifyExpiration(RefreshToken token);
}
