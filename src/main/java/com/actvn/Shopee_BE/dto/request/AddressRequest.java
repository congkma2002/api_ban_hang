package com.actvn.Shopee_BE.dto.request;

import com.actvn.Shopee_BE.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressRequest {
    private String buildingName;
    private String city;
    private String country;
    private String pinCode;
    private String state;
    private String street;
    private int isDefault;
}
