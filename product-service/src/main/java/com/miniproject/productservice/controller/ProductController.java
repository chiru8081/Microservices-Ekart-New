package com.miniproject.productservice.controller;

import com.miniproject.productservice.dto.ProductRequest;
import com.miniproject.productservice.dto.ProductResponse;
import com.miniproject.productservice.model.Product;
import com.miniproject.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest productRequest){

        Product product = productService.createProduct(productRequest);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts(){

        return productService.getAllProducts();
    }

    @GetMapping("/{skuCode}")
    public ResponseEntity<Product> getProductBySkuCode(@PathVariable String skuCode){
        Product productBySkuCode = productService.getProductBySkuCode(skuCode);
        return new ResponseEntity<>(productBySkuCode, HttpStatus.OK);

    }

    @PutMapping("/{skuCode}")
    public ResponseEntity<Product> updateProduct(@PathVariable String skuCode,
                                                 @RequestBody Product updatedProduct){

        Product existingproducts = productService.updateProduct(skuCode, updatedProduct);
        return new ResponseEntity<>(existingproducts, HttpStatus.CREATED);

    }

    @DeleteMapping("/removeProductFromInventory/{skuCode}")
    public ResponseEntity<String> deleteProductFromInventory(@PathVariable String skuCode){

        productService.deleteProductFromInvenory(skuCode);
            return ResponseEntity.ok("Product deleted from inventory");
    }
}
