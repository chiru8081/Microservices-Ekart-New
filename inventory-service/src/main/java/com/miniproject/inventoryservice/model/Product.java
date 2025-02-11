package com.miniproject.inventoryservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Product {

    private String name;
    private String skuCode;
    private String description;
    private BigDecimal price;
    private Integer quantity;
}
