package com.restore.providerservice.repository;

import com.restore.providerservice.entity.NotificationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTypeRepo extends JpaRepository<NotificationTypeEntity, Long> {
}
