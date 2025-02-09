package com.actvn.Shopee_BE.service.impl;

import com.actvn.Shopee_BE.dto.request.AddressRequest;
import com.actvn.Shopee_BE.dto.response.AddressResponse;
import com.actvn.Shopee_BE.dto.response.Response;
import com.actvn.Shopee_BE.entity.Address;
import com.actvn.Shopee_BE.entity.User;
import com.actvn.Shopee_BE.exception.NotFoundException;
import com.actvn.Shopee_BE.repository.AddressRepository;
import com.actvn.Shopee_BE.repository.UserRepository;
import com.actvn.Shopee_BE.service.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Response createAddress(AddressRequest addressDto, User user) {
        Address address = modelMapper.map(addressDto, Address.class);
        address.setUser(user);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        Address saveAdressess = addressRepository.save(address);
        AddressResponse addressResponse = modelMapper.map(saveAdressess, AddressResponse.class);
        return Response.builder()
                .status(HttpStatus.CREATED)
                .body(addressResponse)
                .message("Address đã được tạo")
                .build();
    }

    @Override
    public Response<Object> getAddresses() {
        List<Address> addressList  = addressRepository.findAll();
        List<AddressResponse>  responses = new ArrayList<>();
        if(!addressList.isEmpty()){
        responses = addressList.stream().map(
                address -> modelMapper.map(address, AddressResponse.class)
        ).toList();
        }
        return Response.builder()
                .status(HttpStatus.OK)
                .body(responses)
                .message("Address đã được tạo")
                .build();
    }

    @Override
    public Response<Object> getAddressById(String addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ với id" + addressId));
        AddressResponse response = modelMapper.map(address, AddressResponse.class);
        return Response.builder()
                .status(HttpStatus.OK)
                .body(response)
                .message("Address lấy thành công")
                .build();
    }

    @Override
    public Response<Object> getUserAddresses(User user) {
        List<Address> addresses = user.getAddresses();
        List<AddressResponse> addressResponses = new ArrayList<>();
        if(!addresses.isEmpty()){
            addressResponses = addresses.stream()
                    .map(address -> modelMapper.map(address, AddressResponse.class)).toList();
        }
        return Response.builder()
                .status(HttpStatus.OK)
                .body(addressResponses)
                .message("Address lấy từ user thành công")
                .build();
    }

    @Override
    public Response<Object> updateAddress(String addressId, AddressRequest addressDto) {
        Address foundAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy address có id" + addressId));
        foundAddress.setCity(addressDto.getCity());
        foundAddress.setCountry(addressDto.getCity());
        foundAddress.setState(addressDto.getState());
        foundAddress.setStreet(addressDto.getStreet());
        foundAddress.setBuildingName(addressDto.getBuildingName());
        foundAddress.setPinCode(addressDto.getPinCode());
        foundAddress.setIsDefault(addressDto.getIsDefault());

        Address updateAddress = addressRepository.save(foundAddress);
        User user = foundAddress.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updateAddress);
        userRepository.save(user);

        AddressResponse response = modelMapper.map(updateAddress, AddressResponse.class);

        return Response.builder()
                .status(HttpStatus.OK)
                .body(response)
                .message("Address update thành công")
                .build();
    }

    @Override
    public Response<Object> deleteAddress(String addressId) {
        Address foundAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy address có id" + addressId));
        User user = foundAddress.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);
        addressRepository.delete(foundAddress);

        return Response.builder()
                .status(HttpStatus.OK)
                .body("Address delete successfully with addressId: " + addressId)
                .message("Address delete thành công")
                .build();
    }
}
