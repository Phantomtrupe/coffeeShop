package com.github.phantomtrupe.coffeesystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateTime;

    @ManyToMany
    @JoinTable(
        name = "orders_coffees",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "coffee_id")
    )
    private List<Coffee> coffees;

    private double totalPrice;
    private double estimatedTime;

    public Order() {}

    public Order(Long id, LocalDateTime dateTime, List<Coffee> coffees, double totalPrice, double estimatedTime) {
        this.id = id;
        this.dateTime = dateTime;
        this.coffees = coffees;
        this.totalPrice = totalPrice;
        this.estimatedTime = estimatedTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public List<Coffee> getCoffees() {
        return coffees;
    }

    public void setCoffees(List<Coffee> coffees) {
        this.coffees = coffees;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    @Override
    public String toString() {
        return "Order{" +
               "id=" + id +
               ", dateTime=" + dateTime +
               ", coffees=" + coffees +
               ", totalPrice=" + totalPrice +
               ", estimatedTime=" + estimatedTime +
               '}';
    }
}
