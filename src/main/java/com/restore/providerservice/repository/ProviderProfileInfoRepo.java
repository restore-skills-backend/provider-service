package com.restore.providerservice.repository;

import com.restore.core.entity.ProviderProfileInfoEntity;
import org.hibernate.Internal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderProfileInfoRepo extends JpaRepository<ProviderProfileInfoEntity, Long> {
}
