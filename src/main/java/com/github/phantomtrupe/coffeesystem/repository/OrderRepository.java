package com.github.phantomtrupe.coffeesystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.github.phantomtrupe.coffeesystem.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
