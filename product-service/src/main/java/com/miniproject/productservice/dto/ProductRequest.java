package com.miniproject.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private String skuCode;
    private Integer quantity;
}
