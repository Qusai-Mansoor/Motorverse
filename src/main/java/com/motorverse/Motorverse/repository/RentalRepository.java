package com.motorverse.Motorverse.repository;

import com.motorverse.Motorverse.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
    List<Rental> findByUserIdAndStatus(int userId, Rental.Status status);
}