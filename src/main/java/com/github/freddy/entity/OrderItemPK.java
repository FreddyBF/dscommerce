package com.github.freddy.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderItemPK {
    /*
    Como o OrderItem não tem um ID simples (como 1, 2, 3), mas sim uma Chave Composta (ID do Pedido + ID do Produto),
    precisamos de uma classe auxiliar para a chave.A. A Chave Composta (PK)
    * */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
