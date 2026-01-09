package com.github.freddy.entity;

public enum OrderStatus {

    WAITING_PAYMENT(0),
    PAID(1),
    SHIPPED(2),
    DELIVERED(3),
    CANCELED(4);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código de OrderStatus inválido: " + code);
    }
}

