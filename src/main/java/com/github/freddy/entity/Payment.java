package com.github.freddy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
    private UUID id;
    private Instant paymentDate;

    @PrePersist
    public void prePersist() {
        paymentDate = Instant.now();
    }
}
