package com.github.freddy.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "TB_ORDERS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order { //Pedido do cliente
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant creationDate;

    @Convert(converter =  OrderStatusConverter.class)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @PrePersist
    public void prePersist() {
        /*
        Marca um método que será chamado automaticamente antes
        de salvar uma entidade no banco pela primeira vez (INSERT)
        * */
        creationDate = Instant.now();
    }


}
