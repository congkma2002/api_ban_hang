package com.actvn.Shopee_BE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorMessageResponse {
    private int statusCode;

    private String message;
    private String description;

    private Date timestamp;
}
