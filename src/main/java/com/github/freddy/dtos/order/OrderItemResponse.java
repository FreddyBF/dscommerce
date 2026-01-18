package com.github.freddy.dtos.order;

import com.github.freddy.entity.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        String name,
        BigDecimal price,
        Integer quantity,
        BigDecimal subTotal
) {
    public OrderItemResponse(OrderItem item) {
        this(
             item.getProduct().getName(),
             item.getPrice(),
             item.getQuantity(),
             item.getSubTotal()
        );
    }
}
