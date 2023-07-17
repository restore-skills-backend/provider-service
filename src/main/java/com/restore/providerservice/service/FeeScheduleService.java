package com.restore.providerservice.service;

import com.restore.providerservice.dto.FeeSchedule;
import com.restore.providerservice.dto.FeeScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FeeScheduleService {
    void addFeeSchedule(FeeSchedule feeSchedule);

    Page<FeeScheduleResponse> getFeeScheduleList(String cptCode, Boolean active, Pageable pageable);

    void changeActiveStatus(UUID feeScheduleUuid, boolean activeStatus);

    void deleteFeeSchedule(UUID feeScheduleUuid);

    FeeScheduleResponse updateFeeSchedule(UUID feeScheduleUuid, FeeSchedule feeSchedule);
}
