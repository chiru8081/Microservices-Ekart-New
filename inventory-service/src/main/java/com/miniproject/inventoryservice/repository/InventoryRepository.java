package com.miniproject.inventoryservice.repository;

import com.miniproject.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findBySkuCodeIn(List<String> skuCodes);
    List<Inventory> findBySkuCode(String skuCode);
}
