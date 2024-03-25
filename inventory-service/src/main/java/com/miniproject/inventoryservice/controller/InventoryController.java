package com.miniproject.inventoryservice.controller;

import com.miniproject.inventoryservice.dto.InventoryResponse;
import com.miniproject.inventoryservice.model.Inventory;
import com.miniproject.inventoryservice.model.Product;
import com.miniproject.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    @Autowired
    private final InventoryService inventoryService;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) throws InterruptedException {
        log.info("Received inventory check request for skuCode: {}", skuCode);
        return inventoryService.isInStock(skuCode);
    }

    @PostMapping("/addInventory")
    public ResponseEntity<Inventory> AddToInventory(@RequestBody Product product){

        System.out.println("before adding");
        Inventory inventory = inventoryService.AddToInventory(product);
        System.out.println("after adding");
        return new ResponseEntity<>(inventory, HttpStatus.CREATED);
    }

    @GetMapping("/check/{skuCode}")
    public ResponseEntity<Boolean> checkProductInventory(@PathVariable String skuCode){

        Boolean productInInventory = inventoryService.isProductInInventory(skuCode);
        return new ResponseEntity<>(productInInventory, HttpStatus.OK);
    }

    @DeleteMapping("/deleteProductFromInventory/{skuCode}")
    public ResponseEntity<String> deleteProduct(@PathVariable String skuCode){

        inventoryService.deleteProductFromInventory(skuCode);
        return ResponseEntity.ok("Product deleted from inventory");
    }
}
