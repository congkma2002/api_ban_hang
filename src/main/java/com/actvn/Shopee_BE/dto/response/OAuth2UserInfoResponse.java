package com.actvn.Shopee_BE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OAuth2UserInfoResponse {
    private String name;
    private String email;
    private String avatar;
}
