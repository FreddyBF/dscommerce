package com.github.freddy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "TB_PAYMENTS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private Instant paymentDate;

    @OneToOne
    @JoinColumn(
            name = "order_id",
            unique = true
    )
    Order order;

    @PrePersist
    public void prePersist() {
        paymentDate = Instant.now();
    }
}
