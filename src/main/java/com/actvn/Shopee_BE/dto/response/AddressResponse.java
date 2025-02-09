package com.actvn.Shopee_BE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private String addressId;
    private String buildingName;
    private String city;
    private String country;
    private String pinCode;
    private String state;
    private String street;
    private int isDefault;
}
