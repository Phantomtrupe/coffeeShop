package com.github.phantomtrupe.coffeesystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.github.phantomtrupe.coffeesystem.entity.Coffee;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
}
