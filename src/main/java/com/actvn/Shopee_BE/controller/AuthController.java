package com.actvn.Shopee_BE.controller;

import com.actvn.Shopee_BE.dto.request.*;
import com.actvn.Shopee_BE.dto.response.MessageResponse;
import com.actvn.Shopee_BE.dto.response.OAuth2UserInfoResponse;
import com.actvn.Shopee_BE.dto.response.UserInfoResponse;
import com.actvn.Shopee_BE.entity.AppRole;
import com.actvn.Shopee_BE.entity.RefreshToken;
import com.actvn.Shopee_BE.entity.Role;
import com.actvn.Shopee_BE.exception.TokenRefreshException;
import com.actvn.Shopee_BE.security.jwt.JwtUtils;
import com.actvn.Shopee_BE.security.service.UserDetailsImpl;
import com.actvn.Shopee_BE.service.GoogleAuthService;
import com.actvn.Shopee_BE.service.RefreshTokenService;
import com.actvn.Shopee_BE.service.RoleService;
import com.actvn.Shopee_BE.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import org.slf4j.Logger;

@RestController
@CrossOrigin("http://localhost:3000/")
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private GoogleAuthService googleAuthService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {

            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return ResponseEntity.
                    status(HttpStatus.NOT_FOUND)
                    .body(map);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        ResponseCookie cookie = jwtUtils.generateJwtCookie(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());
        UserInfoResponse response = new UserInfoResponse(
                userDetails.getId(),
                cookie.toString(),
                jwtRefreshCookie.toString(),
                userDetails.getUsername(),
                roles
        );
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if (userService.existByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error : Username is already taken!"));
        }
        if (userService.existByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error : Email is already taken!"));
        }

        UserRequest newUser = new UserRequest(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role role = roleService.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(role);
        }else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "ADMIN":
                        Role adminRole = roleService.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(adminRole);
                        break;
                    case "SELLER":
                        Role sellerRole = roleService.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleService.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                }
            });
        }
        newUser.setRoles(roles);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createNewUser(newUser));
    }

    @PostMapping("/signout")
    public ResponseEntity<MessageResponse> signoutUser(){
        ResponseCookie cleanJwtCookies =  jwtUtils.getCleanJwtCookie();
        ResponseCookie cleanRefreshJwtCookies = jwtUtils.getCleanRefreshJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanJwtCookies.toString())
                .header(HttpHeaders.SET_COOKIE, cleanRefreshJwtCookies.toString())
                .body(new MessageResponse("You've been signout"))
                ;
    }
    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getUserDetail(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities()
                .stream().map(item -> item.getAuthority())
                .toList();
        UserInfoResponse response = new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                roles
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response)
                ;
    }

    @GetMapping("/username")
    public String currentUser(Authentication authentication){
        if(authentication !=null){
            return authentication.getName();
        }
        return "";
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request){
        String refreshToken = jwtUtils.getJwtRefreshFromCookie(request);
        if(refreshToken !=null && refreshToken.length() >0){
            refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
                        return ResponseEntity.status(HttpStatus.OK)
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body(new MessageResponse("Token tạo thành công"))
                                ;
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,"Refresh Token không hợp lệ"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)

                .body(new MessageResponse("Refresh Token is empty"))
                ;
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleCredentialRequest request) throws GeneralSecurityException, IOException {
        String jwtToken = googleAuthService.verifyAndAuthenticate(request.getCredential());

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE,jwtToken)
                .body(jwtToken);
    }
    @PostMapping("/refreshTokenV2")
    public ResponseEntity<?> refreshTokenV2(@RequestBody RefreshTokenRequest request) {
        // Lấy refresh token từ cookie
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateJWTTokenFromUsername(user.getUserName());
                    return ResponseEntity.status(HttpStatus.OK)

                            .body(token);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

}
