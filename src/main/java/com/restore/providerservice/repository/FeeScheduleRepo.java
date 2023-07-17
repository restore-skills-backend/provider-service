package com.restore.providerservice.repository;

import com.restore.providerservice.entity.FeeScheduleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeeScheduleRepo extends JpaRepository<FeeScheduleEntity, Long> {
    Optional<FeeScheduleEntity> findByUuid(UUID feeScheduleUuid);

    @Query(value = "SELECT * FROM fee_schedule WHERE (:cptCode IS NULL OR cpt_code = :cptCode) " +
            "AND (:active IS NULL OR active = :active) AND archive IS FALSE",
            countQuery = "SELECT count(*) FROM fee_schedule WHERE (:cptCode IS NULL OR cpt_code = :cptCode) " +
                    "AND (:active IS NULL OR active = :active) AND archive IS FALSE",
            nativeQuery = true)
    Page<FeeScheduleEntity> findAllByFilter(@Param("cptCode") String cptCode,
                                                    @Param("active") Boolean active,
                                                    Pageable pageable);
}
