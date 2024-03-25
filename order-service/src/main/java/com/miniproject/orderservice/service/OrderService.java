package com.miniproject.orderservice.service;

import com.miniproject.orderservice.dto.InventoryResponse;
import com.miniproject.orderservice.dto.OrderLineItemsDto;
import com.miniproject.orderservice.dto.OrderRequest;
import com.miniproject.orderservice.model.Order;
import com.miniproject.orderservice.model.OrderLineItems;
import com.miniproject.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClient;

    @Autowired
    private RestTemplate restTemplate;
    public String placeOrder(OrderRequest orderRequest){

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        //map(orderLineItemsDto -> maptoDto(orderLineItemsDto))
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDto()
                .stream().map(this::maptoDto)
                .toList();

        order.setOrderLineItems(orderLineItems);

        List<String> skuCodes = order.getOrderLineItems().stream().map(OrderLineItems::getSkuCode).toList();

//        String url = "http://localhost:8082/api/inventory/isInStock";
//
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
//                .queryParam("skuCode", skuCodes.toArray());
//
//        String fullUrl = builder.toUriString();

        //call inventory to check the stock

        InventoryResponse[] inventoryResponses = webClient.build().get()
                .uri("http://inventory-service/api/inventory"
                        , uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                        .retrieve()
                                .bodyToMono(InventoryResponse[].class)
                                        .block();
//
//        InventoryResponse[] inventoryResponses = restTemplate.
//                getForObject(fullUrl, InventoryResponse[].class);

//        ResponseEntity<InventoryResponse[]> inventoryResponses = restTemplate.exchange(
//                fullUrl,
//                HttpMethod.GET,
//                null,  // Request entity (e.g., headers or body)
//                InventoryResponse[].class);

        boolean allProductsIsInStock= Arrays.stream(inventoryResponses)
                .allMatch(InventoryResponse::isInStock);

        if(allProductsIsInStock){
            orderRepository.save(order);
            return "order placed successfully";
        }else{
            return "Order is not in stock try again later";
        }

    }

    private OrderLineItems maptoDto(OrderLineItemsDto orderLineItemsDto) {

        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        return orderLineItems;
    }

    public void updateOrderStatus(String orderNumber) {

        Optional<Order> optionalOrder = orderRepository.findByOrderNumber(orderNumber);
        Order order = optionalOrder.get();
        List<OrderLineItems> orderLineItems = optionalOrder.get().getOrderLineItems();

        if (optionalOrder.isPresent()) {

            for(OrderLineItems items : orderLineItems){
                String skuCode = items.getSkuCode();
                boolean isProductInInventory = isProductInInventory(skuCode);

                if(isProductInInventory){
                    order.setStatus("shipped");
                    orderRepository.save(order);
                }else{
                    System.out.println("skuCode " + skuCode + " is not in inventory, so unable to update");
                }
            }
        }else{
            System.out.println("order with orderNumber " + orderNumber + "not present");
        }
    }
    public boolean isProductInInventory(String skuCode) {

        String inventory_url = "http://inventory-service/api/inventory/check/{skuCode}";
        return restTemplate.getForObject(inventory_url, Boolean.class, skuCode);
    }
}
