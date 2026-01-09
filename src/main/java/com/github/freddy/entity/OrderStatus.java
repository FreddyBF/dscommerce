package com.github.freddy.entity;

public enum OrderStatus {
    WAITING_PAYMENT,
    PAID, // Pagamento aprovado
    SHIPPED, //Pedido enviado para entrega
    DELIVERED, // Pedido entregue ao cliente
    CANCELED; //Pedido cancelado (antes da entrega)
}

