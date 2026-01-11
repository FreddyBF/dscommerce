package com.github.freddy.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_ORDER_ITEM")
public class OrderItem {

    @EmbeddedId
    private OrderItemPK id = new OrderItemPK();

    private Integer quantity;

    private BigDecimal price;

    public OrderItem() {}

    public OrderItem(Order order, Product product, Integer quantity, BigDecimal price) {
        id.setOrder(order);
        id.setProduct(product);
        this.quantity = quantity;
        this.price = price;
    }

    public BigDecimal getSubTotal() {
        return price.multiply(new BigDecimal(quantity));
    }

    public Integer getQuantity() {
        return quantity;
    }

    public  void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public  BigDecimal getPrice() {
        return price;
    }

    public  void setPrice(BigDecimal price) {
        this.price = price;
    }
    // Métodos auxiliares para acessar a chave composta
    public Order getOrder() {
        return id.getOrder();
    }

    public void setOrder(Order order) {
        id.setOrder(order);
    }

    public Product getProduct() {
        return id.getProduct();
    }
    public void setProduct(Product product) {
        id.setProduct(product);
    }


    // equals, hashCode e toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        OrderItem that = (OrderItem) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

