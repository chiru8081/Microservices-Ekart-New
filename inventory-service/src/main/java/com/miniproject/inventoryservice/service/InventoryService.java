package com.miniproject.inventoryservice.service;

import com.miniproject.inventoryservice.customExceptionHandler.ProductNotFoundException;
import com.miniproject.inventoryservice.dto.InventoryResponse;
import com.miniproject.inventoryservice.model.Inventory;
import com.miniproject.inventoryservice.model.Product;
import com.miniproject.inventoryservice.repository.InventoryRepository;
import jakarta.persistence.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    @Autowired
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) throws InterruptedException {
        log.info("wait started");
        Thread.sleep(10000);
        log.info("wait ended");
        System.out.println("finding the stock");
        List<InventoryResponse> responseList = inventoryRepository.findBySkuCodeIn(skuCode)
                .stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()).toList();

        System.out.println("Received skuCodes: " + skuCode);
//        System.out.println("Received isInStock: " + isInStock(skuCode));
        System.out.println("response " + responseList);
        for (InventoryResponse response : responseList) {
            // Perform actions for each InventoryResponse item
            System.out.println("SKU Code: " + response.getSkuCode() + ", In Stock: " + response.isInStock());
        }
        return responseList;
    }

    public Inventory AddToInventory(Product productRequest){

        Inventory inventory = Inventory.builder()
                .skuCode(productRequest.getSkuCode())
                .quantity(productRequest.getQuantity())
                .build();
        Inventory inventoryDetials = inventoryRepository.save(inventory);

        log.info("Product {} added to inventory " + inventory.getId());
        return inventoryDetials;
    }

    public Boolean isProductInInventory(String skuCode) {

        System.out.println("checking in inventory");
        Inventory inventory = getInventoryItemBySkuCode(skuCode);
        return inventory != null && inventory.getQuantity() > 0;
    }

    private Inventory getInventoryItemBySkuCode(String skuCode) {

        List<Inventory> inventoryList = inventoryRepository.findBySkuCode(skuCode);

        if(inventoryList.isEmpty()){
            return null;
        }else if(inventoryList.size() > 1){
            throw new NonUniqueResultException("Multiple products found for the skuCode " + skuCode + " in inventory");
        } else {
            return inventoryList.get(0);
        }
    }

    public boolean deleteProductFromInventory(String skuCode) {

       Inventory inventory =  getInventoryItemBySkuCode(skuCode);
       if(inventory!=null && inventory.getQuantity() > 0){
           inventoryRepository.delete(inventory);
           return true;
       }else{
           throw new ProductNotFoundException("Product not found with skuCode " + skuCode);
       }
    }
}
