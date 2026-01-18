package com.github.freddy.exceptions;

// Representa que a operação é válida tecnicamente, mas proibida pela regra de negócio
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}