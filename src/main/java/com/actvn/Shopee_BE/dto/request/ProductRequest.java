package com.actvn.Shopee_BE.dto.request;

import com.actvn.Shopee_BE.entity.Category;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductRequest {
    private String productName;
    private String description;
    private double discount;
    private double price;
    private String image;
    private int quantity;
    private double specialPrice;
}
