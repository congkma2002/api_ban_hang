package com.actvn.Shopee_BE.service.impl;

import com.actvn.Shopee_BE.entity.RefreshToken;
import com.actvn.Shopee_BE.repository.RefreshTokenRepository;
import com.actvn.Shopee_BE.repository.UserRepository;
import com.actvn.Shopee_BE.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${spring.app.jwtRefreshExpirationMs}")
    private long jwtRefreshExpirationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
            return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(String userId) {
        RefreshToken refreshTokenFound = refreshTokenRepository.findByUser(userRepository.findById(userId).get());
        if(refreshTokenFound != null){
            refreshTokenRepository.delete(refreshTokenFound);
        }
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtRefreshExpirationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void deleteByUserId(String userId) {
        refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token.getExpiryDate().compareTo(Instant.now())< 0){
            refreshTokenRepository.delete(token);
        }else {

        }
        return token;
    }
}
