package com.github.freddy.services;


import com.github.freddy.dtos.payment.PaymentDTO;
import com.github.freddy.entity.Order;
import com.github.freddy.entity.OrderStatus;
import com.github.freddy.entity.Payment;
import com.github.freddy.exceptions.ResourceNotFoundException;
import com.github.freddy.repositories.OrderRepository;
import com.github.freddy.repositories.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentDTO payOrder(UUID orderId) {
        // Buscar pedido
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Verificar se já foi pago
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Order is already paid");
        }

        // Validação: Está cancelado?
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("Cannot pay a canceled order");
        }
        //Criar pagamento
        Payment payment = new Payment();
        payment.setOrder(order);
        payment = paymentRepository.save(payment);

        order.setPayment(payment);
        // Actualizar status do pedido
        order.setStatus(OrderStatus.PAID);

        // Retornar DTO
        return new PaymentDTO(
                payment.getId(),
                order.getId(),
                order.getTotal(),
                payment.getPaymentDate()
        );
    }
}
