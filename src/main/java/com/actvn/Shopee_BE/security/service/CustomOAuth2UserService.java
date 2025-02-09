package com.actvn.Shopee_BE.security.service;

import com.actvn.Shopee_BE.dto.request.UserRequest;
import com.actvn.Shopee_BE.dto.response.Response;
import com.actvn.Shopee_BE.entity.User;
import com.actvn.Shopee_BE.security.jwt.JwtUtils;
import com.actvn.Shopee_BE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

}
