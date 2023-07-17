package com.restore.providerservice.repository;

import com.restore.core.entity.ProviderEntity;
import com.restore.core.entity.StaffEntity;
import com.restore.core.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepo extends JpaRepository<StaffEntity, Long> {
    Optional<StaffEntity> findByUuid(UUID uuid);
    StaffEntity findByUserId(UserEntity userEntity);
    Page<StaffEntity> findAllByArchiveIsFalse(Pageable pageable);
}
