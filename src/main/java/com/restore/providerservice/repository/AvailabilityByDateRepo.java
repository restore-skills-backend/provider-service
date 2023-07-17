package com.restore.providerservice.repository;

import com.restore.core.entity.LocationEntity;
import com.restore.providerservice.entity.AvailabilityByDateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityByDateRepo extends JpaRepository<AvailabilityByDateEntity, Long> {
    List<AvailabilityByDateEntity> findAllByLocationEntity(LocationEntity locationEntity);
}
