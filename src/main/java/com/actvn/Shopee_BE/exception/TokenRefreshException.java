package com.actvn.Shopee_BE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenRefreshException extends RuntimeException {
    private static final long SERIAL_VERSION_ID = 1L;

    public TokenRefreshException(String token, String message) {
        super("Failed for" + token + message);
    }
}
