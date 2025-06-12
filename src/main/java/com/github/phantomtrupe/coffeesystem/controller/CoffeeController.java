
package com.github.phantomtrupe.coffeesystem.controller;

import com.github.phantomtrupe.coffeesystem.repository.CoffeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.github.phantomtrupe.coffeesystem.entity.Coffee;
import java.util.List;

@RestController
@RequestMapping("/api/coffees")
public class CoffeeController {
    private final CoffeeRepository coffeeRepo;

    public CoffeeController(CoffeeRepository coffeeRepo) {
        this.coffeeRepo = coffeeRepo;
    }

    @GetMapping
    public ResponseEntity<List<Coffee>> getAllCoffees() {
        return ResponseEntity.ok(coffeeRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coffee> getCoffeeById(@PathVariable Long id) {
        return coffeeRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Coffee> createCoffee(@RequestBody Coffee coffee) {
        if (coffee.getName() == null || coffee.getName().isBlank() || coffee.getPrice() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Coffee saved = coffeeRepo.save(coffee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoffee(@PathVariable Long id) {
        if (!coffeeRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        coffeeRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
