package com.github.freddy.controllers;

import com.github.freddy.dtos.OrderDTO;
import com.github.freddy.dtos.OrderInputDTO;
import com.github.freddy.entity.Payment;
import com.github.freddy.services.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody @Valid OrderInputDTO orderDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderDTO));
    }
    @GetMapping("/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrder(@PathVariable UUID userId, @RequestParam int page, @RequestParam int size){
        Pageable  pageable = PageRequest.of(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrdersByUser(userId, pageable));
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders(@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders(pageable));
    }

    @DeleteMapping
    public void deleteOrder(@PathVariable UUID orderId) {
        orderService.deleteOrder(orderId);
    }

    @PostMapping("/{orderId}/payment")
    public ResponseEntity<OrderDTO> payOrder(@PathVariable UUID orderId){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.payOrder(orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable UUID orderId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.cancelOrder(orderId));
    }
}
