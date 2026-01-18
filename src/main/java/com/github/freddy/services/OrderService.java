package com.github.freddy.services;

import com.github.freddy.dtos.*;
import com.github.freddy.dtos.order.OrderDTO;
import com.github.freddy.dtos.order.OrderItemResponse;
import com.github.freddy.dtos.order.OrderRequestDTO;
import com.github.freddy.entity.*;
import com.github.freddy.exceptions.BusinessException;
import com.github.freddy.exceptions.ResourceNotFoundException;
import com.github.freddy.repositories.OrderRepository;
import com.github.freddy.repositories.ProductRepository;
import com.github.freddy.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final AuthService authService;

    @Transactional
    public OrderDTO createOrder(OrderRequestDTO orderDTO) {
        User user = authService.authenticated();

        //Otimização de Performance (Evita N+1)
        List<UUID> productIds = orderDTO.items().stream()
                .map(OrderRequestDTO.OrderItem::productId)
                .toList();

        List<Product> products = productRepository.findAllById(productIds);
        // Valida se todos os produtos foram achados
        if (products.size() != productIds.size()) {
            throw new BusinessException("One or more product not found");
        }
        // Mapa para acesso rápido
        Map<UUID, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        Order order = new Order();
        order.setClient(user);
        order.setStatus(OrderStatus.WAITING_PAYMENT);

        //Processamento em Memória
        for (OrderRequestDTO.OrderItem itemDTO : orderDTO.items()) {
            Product product = productMap.get(itemDTO.productId());

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.quantity());
            orderItem.setPrice(product.getPrice()); // Snapshot do preço
            orderItem.setOrder(order);
            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        return new OrderDTO(
                savedOrder,
                savedOrder.getItems().stream().map(OrderItemResponse::new).toList()
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderDTO> getOrdersByUserId(UUID userId, Pageable pageable) {

        Page<Order> orderPage = orderRepository.findByClientId(userId, pageable);

        List<OrderDTO> listDTO = new ArrayList<>();

        if (orderPage.hasContent()) {
            List<Order> orderList = orderRepository.fetchOrdersWithItems(orderPage.getContent());
            listDTO = orderList.stream()
                    .map(o -> new OrderDTO(
                            o.getId(),
                            o.getStatus().name(),
                            o.getCreationDate().toString(),
                            o.getTotal(),
                            o.getItems().stream()
                                    .map(OrderItemResponse::new)
                                    .toList()
                    )).toList();

        }

        return new PageResponse<>(
                listDTO,
                new PageResponse.PaginationMetadata(
                    orderPage.getNumber(),
                    orderPage.getSize(),
                    orderPage.getTotalElements(),
                    orderPage.getTotalPages(),
                    orderPage.hasNext(),
                    orderPage.hasPrevious()
                )
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderDTO> getAllOrders(Pageable pageable) {
        //Busca os pedidos paginados (Query 1)
        Page<Order> orderPage = orderRepository.findAll(pageable);

        List<Order> ordersWithItems = orderRepository.fetchOrdersWithItems(orderPage.getContent());

        // Converte a lista hidratada para DTO
        List<OrderDTO> dtos = ordersWithItems.stream()
                .map(order -> new OrderDTO(order, order.getItems().stream().map(OrderItemResponse::new)
                        .toList())
                ).toList();

        //Cria o PageResponse manual usando os dados da orderPage original
        return new PageResponse<>(
                dtos,
                new PageResponse.PaginationMetadata(
                        orderPage.getNumber(),
                        orderPage.getSize(),
                        orderPage.getTotalElements(),
                        orderPage.getTotalPages(),
                        orderPage.hasNext(),
                        orderPage.hasPrevious()
                )
        );
    }

    @Transactional(readOnly = true)
    // Buscar pedido pelo ID
    public Order getOrderById(UUID orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Transactional
    // Deletar pedido e o pagamento será removido automaticamente
    public void deleteOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        orderRepository.delete(order); // Payment será removido automaticamente
    }


    @Transactional
    public OrderDTO cancelOrder(UUID orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            throw new BusinessException("Order cannot be canceled. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELED);
        order = orderRepository.save(order);
        return new OrderDTO(order, order.getItems().stream().map(OrderItemResponse::new).toList());
    }
}

