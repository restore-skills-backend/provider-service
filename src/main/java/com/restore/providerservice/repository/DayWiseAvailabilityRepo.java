package com.restore.providerservice.repository;

import com.restore.providerservice.entity.DayWiseAvailabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayWiseAvailabilityRepo extends JpaRepository<DayWiseAvailabilityEntity, Long> {
}
