package com.actvn.Shopee_BE.service;

import com.actvn.Shopee_BE.dto.request.AddressRequest;
import com.actvn.Shopee_BE.dto.response.Response;
import com.actvn.Shopee_BE.entity.User;

public interface AddressService {
    Response<Object> createAddress(AddressRequest addressDto, User user);

    Response<Object> getAddresses();

    Response<Object> getAddressById(String addressId);

    Response<Object> getUserAddresses(User user);


    Response<Object> updateAddress(String addressId, AddressRequest addressDto);

    Response<Object> deleteAddress(String addressId);
}
