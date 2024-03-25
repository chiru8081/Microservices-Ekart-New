package com.miniproject.productservice.service;

import com.miniproject.productservice.customExceptionHandler.ProductNotFoundException;
import com.miniproject.productservice.dto.ProductRequest;
import com.miniproject.productservice.dto.ProductResponse;
import com.miniproject.productservice.model.Product;
import com.miniproject.productservice.repository.ProductRepository;
import jakarta.persistence.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;
    public Product createProduct(ProductRequest productRequest){

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .skuCode(productRequest.getSkuCode())
                .quantity(productRequest.getQuantity()).build();

        productRepository.save(product);

        log.info("product {} is saved " + product.getId());

        sendProductToInventory(product);

        return product;
    }

    public Product updateProduct(String skuCode, Product updatedProduct){
        Product existingProduct = getProductBySkuCode(skuCode);
        if(existingProduct == null){
            throw new ProductNotFoundException("product not found with the skuCode " + skuCode);
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setSkuCode(updatedProduct.getSkuCode());

        productRepository.save(existingProduct);
        return existingProduct;
    }
    private void sendProductToInventory(Product product) {

        String url = "http://inventory-service/api/inventory/addInventory";
        restTemplate.postForObject(url, product, Product.class);
    }

    public List<ProductResponse> getAllProducts(){

        List<Product> products = productRepository.findAll();

        //products.stream().map(product -> maptoProductResponse(product)).toList();
        return  products.stream().map(this::maptoProductResponse).toList();
    }

    private ProductResponse maptoProductResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .skuCode(product.getSkuCode())
                .build();
    }

    public Product getProductBySkuCode(String skuCode){
        List<Product> product = productRepository.findBySkuCode(skuCode);

        if (product.isEmpty()) {
            // Handle case where no product is found
            return null;
        } else if (product.size() > 1) {
            // Handle case where multiple products are found (log, throw exception, etc.)
            throw new NonUniqueResultException("Multiple products found for SKU code: " + skuCode);
        } else {
            return product.get(0);
        }
    }

    public void deleteProductFromInvenory(String skuCode) {

        Product productBySkuCode = getProductBySkuCode(skuCode);
        if(productBySkuCode!= null && productBySkuCode.getQuantity() > 0){
            productRepository.delete(productBySkuCode);
            //give it as string just like order service
        }else{
            throw new ProductNotFoundException("product not found with the skuCode " + skuCode);
        }


        String inventoryServiceUrl = "http://inventory-service/api/inventory/deleteProductFromInventory/{skuCode}";

        restTemplate.exchange(
                inventoryServiceUrl,
                HttpMethod.DELETE,
                null,
                void.class,
                skuCode
        );
    }
}
