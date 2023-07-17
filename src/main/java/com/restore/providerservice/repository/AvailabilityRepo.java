package com.restore.providerservice.repository;

import com.restore.core.entity.LocationEntity;
import com.restore.providerservice.entity.AvailabilityEntity;
import com.restore.core.entity.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvailabilityRepo extends JpaRepository<AvailabilityEntity, Long> {
    AvailabilityEntity findByLocationEntityAndProviderEntity(LocationEntity locationEntity, ProviderEntity providerEntity);

    AvailabilityEntity findByLocationEntity(LocationEntity locationEntity);

    Optional<AvailabilityEntity> findByUuid(UUID uuid);

    List<AvailabilityEntity> findAllByProviderEntity(ProviderEntity providerEntity);

    List<AvailabilityEntity> findAllByProviderEntityAndLocationEntityNot(ProviderEntity providerEntity, LocationEntity locationEntity);
}
