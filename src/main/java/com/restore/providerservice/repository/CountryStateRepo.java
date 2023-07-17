package com.restore.providerservice.repository;

import com.restore.core.entity.CountryStateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryStateRepo extends JpaRepository<CountryStateEntity, Long> {
    Optional<CountryStateEntity> findByState(String state);
    Page<CountryStateEntity> findAll(Pageable pageable);
}
