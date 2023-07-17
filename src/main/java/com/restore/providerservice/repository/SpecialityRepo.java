package com.restore.providerservice.repository;

import com.restore.core.entity.SpecialityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialityRepo extends JpaRepository<SpecialityEntity, Long> {
    Optional<SpecialityEntity> findByName(String name);
    Page<SpecialityEntity> findAll(Pageable pageable);
}
