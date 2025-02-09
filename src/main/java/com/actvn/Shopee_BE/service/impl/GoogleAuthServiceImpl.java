package com.actvn.Shopee_BE.service.impl;

import com.actvn.Shopee_BE.entity.AppRole;
import com.actvn.Shopee_BE.entity.Role;
import com.actvn.Shopee_BE.entity.User;
import com.actvn.Shopee_BE.repository.UserRepository;
import com.actvn.Shopee_BE.security.jwt.JwtUtils;
import com.actvn.Shopee_BE.service.GoogleAuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class GoogleAuthServiceImpl implements GoogleAuthService {
    private static final String CLIENT_ID = "339674747826-9fnerdg3r6k5b183tusj30icj580q01b.apps.googleusercontent.com";

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;

    @Override
    public String verifyAndAuthenticate(String credential) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), new GsonFactory()
        )
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
        GoogleIdToken token = verifier.verify(credential);
        if (token != null) {
            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();
            String userName = (String) payload.get("name");

            return handleOAuth2Login(userName, email);
        }else {
            throw new IllegalArgumentException("GOOgle không hợp lệ");
        }
    }

    public String handleOAuth2Login(String userName, String email) {
        User user = processOAuthPostLogin(userName, email);
        return jwtUtils.generateJWTTokenFromUsername(user.getUserName());
    }

    private User processOAuthPostLogin(String userName, String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User();
            newUser.setUserName(userName);
            newUser.setEmail(email);
            newUser.setPassword(null);
            newUser.setRoles(Set.of(new Role(AppRole.ROLE_USER)));
            return userRepository.save(newUser);
        }
    }
}
