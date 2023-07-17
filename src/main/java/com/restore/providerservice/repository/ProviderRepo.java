package com.restore.providerservice.repository;

import com.restore.core.entity.UserEntity;
import com.restore.core.entity.ProviderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderRepo extends JpaRepository<ProviderEntity, Long> {
//    Page<Provider> findByNameContainingIgnoreCaseAndCreatedAndIsActive(String name, Instant date, boolean isActive, Pageable pageable);
//    Page<Provider>  findByCreatedBetween(Instant today,Instant tommorrow, Pageable pageable);
//    Page<Provider> findByNameContainingIgnoreCaseAndIsActiveAndCreatedBetweenAndSpecialityType(String name, boolean isActive,Instant today,Instant tommorrow, ProviderSpeciality providerSpeciality,Pageable pageable);
//    Page<Provider> findByNameContainingIgnoreCaseAndCreatedBetweenAndSpecialityType(String name,Instant today,Instant tommorrow,ProviderSpeciality providerSpeciality, Pageable pageable);
//    Page<ProviderEntity> findByNameContainingIgnoreCaseAndActiveAndSpecialityTypes(String name, Boolean isActive, ProviderSpeciality providerSpeciality, Pageable pageable);
//    Page<ProviderEntity> findByNameContainingIgnoreCaseOrSpecialityTypes(String name, ProviderSpeciality providerSpeciality, Pageable pageable);

    ProviderEntity findByUserId(UserEntity userId);
//    Page<ProviderEntity> findByNameContainingIgnoreCaseAndActiveAndSpecialityTypes(String name, Boolean isActive, ProviderSpeciality providerSpeciality, Pageable pageable);
//    Page<ProviderEntity> findByNameContainingIgnoreCaseOrSpecialityTypes(String name, ProviderSpeciality providerSpeciality, Pageable pageable);
    Optional<ProviderEntity> findByUuid(UUID uuid);
    Optional<ProviderEntity> findByNpiNumber(Long npiNumber);
    Page<ProviderEntity> findByArchiveIsFalse(Pageable pageable);
}
