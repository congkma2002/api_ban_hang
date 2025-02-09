package com.actvn.Shopee_BE.advice;

import com.actvn.Shopee_BE.dto.response.ErrorMessageResponse;
import com.actvn.Shopee_BE.exception.TokenRefreshException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class TokenControllerAdvice {
    public ErrorMessageResponse handleRefreshTokenException(TokenRefreshException exception, WebRequest request){
        return new ErrorMessageResponse(
                HttpStatus.FORBIDDEN.value(),
                exception.getMessage(),
                request.getDescription(false),
                new Date()
        );
    }
}
