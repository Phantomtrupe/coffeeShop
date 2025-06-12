package com.github.phantomtrupe.coffeesystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import com.github.phantomtrupe.coffeesystem.entity.Order;
import com.github.phantomtrupe.coffeesystem.entity.Coffee;
import com.github.phantomtrupe.coffeesystem.repository.OrderRepository;
import com.github.phantomtrupe.coffeesystem.repository.CoffeeRepository;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository orderRepo;
    private final CoffeeRepository coffeeRepo;

    public OrderController(OrderRepository orderRepo, CoffeeRepository coffeeRepo) {
        this.orderRepo = orderRepo;
        this.coffeeRepo = coffeeRepo;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody List<Long> coffeeIds) {
        if (coffeeIds == null || coffeeIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Coffee> coffees = new ArrayList<>();
        for (Long id : coffeeIds) {
            var opt = coffeeRepo.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            coffees.add(opt.get());
        }
        Order order = new Order();
        order.setDateTime(LocalDateTime.now());
        order.setCoffees(coffees);
        double total = coffees.stream().mapToDouble(Coffee::getPrice).sum();
        order.setTotalPrice(total);
        double eta = coffees.stream().mapToDouble(Coffee::getEstimatedTime).sum();
        order.setEstimatedTime(eta);
        Order saved = orderRepo.save(order);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepo.findAll(Sort.by(Sort.Direction.DESC, "dateTime"));
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (!orderRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
