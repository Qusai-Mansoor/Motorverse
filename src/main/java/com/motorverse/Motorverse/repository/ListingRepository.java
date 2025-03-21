package com.motorverse.Motorverse.repository;

import com.motorverse.Motorverse.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Integer> {
    List<Listing> findByListingTypeAndStatus(Listing.ListingType type, Listing.ListingStatus status);
}
