package com.github.freddy.controllers;

import com.github.freddy.dtos.order.OrderDTO;
import com.github.freddy.dtos.order.OrderRequestDTO;
import com.github.freddy.dtos.PageResponse;
import com.github.freddy.dtos.payment.PaymentDTO;
import com.github.freddy.entity.User;
import com.github.freddy.services.OrderService;
import com.github.freddy.services.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody @Valid OrderRequestDTO orderDTO){
        OrderDTO order = orderService.createOrder(orderDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/orders/{id}")
                .buildAndExpand(order.id())
                .toUri();
        return ResponseEntity.created(location).body(order);
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<PageResponse<OrderDTO>> getOrder(@PathVariable UUID userId, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrdersByUserId(userId, pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<OrderDTO>> getAllOrders(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders(pageable));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orderId}/payments")
    public ResponseEntity<PaymentDTO> payOrder(@PathVariable UUID orderId, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.payOrder(orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable UUID orderId){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.cancelOrder(orderId));
    }
}
