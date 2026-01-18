package com.github.freddy.repositories;

import com.github.freddy.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Passo 1: Busca apenas os pedidos (paginado) - Sem o Join Fetch aqui!
    Page<Order> findByClientId(UUID userId, Pageable pageable);


    // Passo 2: Busca os itens detalhados apenas para os IDs da página atual
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o in :orders")
    List<Order> fetchOrdersWithItems(@Param("orders") List<Order> orders);


    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") UUID orderId);



}
