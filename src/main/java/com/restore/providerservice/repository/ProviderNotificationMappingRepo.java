package com.restore.providerservice.repository;

import com.restore.providerservice.entity.NotificationTypeEntity;
import com.restore.core.entity.ProviderEntity;
import com.restore.providerservice.entity.ProviderNotificationMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderNotificationMappingRepo extends JpaRepository<ProviderNotificationMappingEntity, Long> {
    Optional<ProviderNotificationMappingEntity> findByProviderEntityAndNotificationTypeEntity(ProviderEntity providerEntity, NotificationTypeEntity notificationTypeEntity);

    List<ProviderNotificationMappingEntity> findAllByProviderEntity(ProviderEntity providerEntity);
}
