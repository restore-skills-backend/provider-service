package com.restore.providerservice.repository;

import com.restore.core.entity.InsurancePayerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsurancePayerRepo extends JpaRepository<InsurancePayerEntity, Long> {
    Page<InsurancePayerEntity> findAll(Pageable pageable);
}
