package com.actvn.Shopee_BE.controller;

import com.actvn.Shopee_BE.dto.request.AddressRequest;
import com.actvn.Shopee_BE.dto.response.Response;
import com.actvn.Shopee_BE.entity.User;
import com.actvn.Shopee_BE.service.AddressService;
import com.actvn.Shopee_BE.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtils authUtils;

    @PostMapping("/addresses")
    public ResponseEntity<Response> createAddress(@RequestBody AddressRequest addressDto){
        User user = authUtils.getUserLogger();
        Response response = addressService.createAddress(addressDto, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("/addresses")
    public ResponseEntity<Response> getAddresses(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(addressService.getAddresses());
    }
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<Response> getAddressById(@PathVariable String addressId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(addressService.getAddressById(addressId));
    }
    @GetMapping("/users/addresses")
    public ResponseEntity<Response> getUserAddresses(){
        User user = authUtils.getUserLogger();

        return ResponseEntity.status(HttpStatus.OK)
                .body(addressService.getUserAddresses(user));
    }
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<Response> updateAddress(@PathVariable String addressId, @RequestBody AddressRequest addressRequest){

        return ResponseEntity.status(HttpStatus.OK)
                .body(addressService.updateAddress(addressId, addressRequest));
    }
    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<Response> deleteAddress(@PathVariable String addressId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(addressService.deleteAddress(addressId));
    }
}
