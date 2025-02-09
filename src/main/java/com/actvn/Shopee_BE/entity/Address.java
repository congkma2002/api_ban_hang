package com.actvn.Shopee_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Table(name = "addresses")
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String addressId;
    @Column(name = "building_name")
    private String buildingName;
    private String city;
    private String country;
    @Column(name = "pin_code")
    private String pinCode;
    private String state;
    private String street;

    @Column(name = "is_default")
    private int isDefault;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    public Address(String addressid, String city, String buildingName, String country, String pinCode, String state, String street, int isDefault) {
        this.addressId = addressid;
        this.city = city;
        this.buildingName = buildingName;
        this.country = country;
        this.pinCode = pinCode;
        this.state = state;
        this.street = street;
        this.isDefault = isDefault;
    }
}
