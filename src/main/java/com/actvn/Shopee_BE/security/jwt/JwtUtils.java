package com.actvn.Shopee_BE.security.jwt;

import com.actvn.Shopee_BE.entity.User;
import com.actvn.Shopee_BE.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

import org.springframework.web.util.WebUtils;

@Slf4j
@Component
public class JwtUtils {

//    private final static Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecretKey;
    @Value("${spring.app.jwtExpiration}")
    private long jwtExpiration;
    @Value("${spring.app.jwtRefreshExpirationMs}")
    private long jwtRefreshExpirationMs;
    @Value("${spring.app.jwtCookieName}")
    private String jwtCookieName;
    @Value("${spring.app.jwtRefreshCookieName}")
    private String jwtRefreshCookieName;

    private static final String REFRESH_TOKEN_API_PATH = "/api/auth/refreshToken";

    public String generateJWTTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();

       return generateJWTTokenFromUsername(username);
    }
    public String generateJWTTokenFromUsername(String userName) {
       long expiration = (new Date()).getTime() + jwtExpiration;
        return Jwts.builder()
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(expiration))
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //lay token tu header client gui ve
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.debug("Authorization: header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // remove "Bearer" prefix
        }
        return null;
    }

    public boolean validateJwtToken(String authToken) {
        log.debug("validate jwt token", authToken);
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException exception) {
            log.error("Invalid  JWT token: {}", exception.getMessage());
        } catch (ExpiredJwtException exception) {
            log.error("JWT token is expired: {}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.error("JWT is unsupported: {}", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("JWT claims string is empty: {}", exception.getMessage());
        }
        return false;
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl userDetails) {
        String jwt = generateJWTTokenFromUsername(userDetails);
        ResponseCookie cookie = generateCookie(jwtCookieName, jwt, "api");
        return cookie;
    }

    public ResponseCookie generateJwtCookie(User user) {
        String jwt = generateJWTTokenFromUsername(user.getUserName());
        ResponseCookie cookie = generateCookie(jwtCookieName, jwt, "/api");
        return cookie;
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookieName, null)
                .path("api")
                .build();
    }

    public ResponseCookie getCleanRefreshJwtCookie() {
        ResponseCookie cookie = ResponseCookie.from(jwtCookieName, null)
                .path(REFRESH_TOKEN_API_PATH)
                .build();
        return cookie;
    }

//    public String getJwtFromCookie(HttpServletRequest request) {
//        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
//        if (cookie != null) {
//            return cookie.getValue();
//        }
//        return null;
//    }

    public String getJwtRefreshFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtRefreshCookieName);
        return (cookie != null) ? cookie.getValue() : null;
    }

    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return generateCookie(jwtRefreshCookieName, refreshToken, REFRESH_TOKEN_API_PATH);
    }

    private ResponseCookie generateCookie(String name, String value, String path) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path(path)
                .maxAge(24 * 60 * 60)
                .build();
        return cookie;
    }
}