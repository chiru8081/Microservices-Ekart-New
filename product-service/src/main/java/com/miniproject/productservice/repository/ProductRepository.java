package com.miniproject.productservice.repository;

import com.miniproject.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findBySkuCode(String skuCode);
}
