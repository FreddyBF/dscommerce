package com.github.freddy.dtos;

import com.github.freddy.entity.Order;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public record OrderDTO(
        UUID id,
        String status,
        String createdDate,
        BigDecimal total,
        List<OrderItemResponse> itemsOrder
) {

    public OrderDTO(Order order, List<OrderItemResponse> items) {
        this(
                order.getId(),
                order.getStatus().name().toLowerCase(),
                order.getCreationDate()
                        .atZone(ZoneId.of("GT+1"))
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                order.getTotal(),
                items
        );
    }
}
