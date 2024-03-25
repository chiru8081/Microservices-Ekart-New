package com.miniproject.orderservice.controller;

import com.miniproject.orderservice.dto.OrderRequest;
import com.miniproject.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private final OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest){
        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequest));

        //CompletableFuture : It is used to perform async communication
        // Future: it is mechanism that is implemented in async communication.
        //some other mechanism are ExcecutorService, callback etc

        //        if(result.contains("successfully")){
//            return ResponseEntity.ok(result);
//        }else{
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
//        }
    }

    @PutMapping("/{orderNumber}/updateIfInInventory")
    public ResponseEntity<String> updateOrderStatus(@PathVariable String orderNumber){

        orderService.updateOrderStatus(orderNumber);
        return ResponseEntity.ok("Order update initiated. Check logs for details.");
    }

    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(() -> "oops something went wrong....Please try again after sometime");
    }
}
