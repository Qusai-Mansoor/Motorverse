package com.motorverse.Motorverse.repository;

import com.motorverse.Motorverse.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
}