package com.restore.providerservice.repository;

import com.restore.providerservice.entity.BlockDayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockDayRepo extends JpaRepository<BlockDayEntity, Long> {
}
