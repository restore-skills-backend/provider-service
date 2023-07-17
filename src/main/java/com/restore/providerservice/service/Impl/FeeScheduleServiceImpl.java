package com.restore.providerservice.service.Impl;

import com.restore.providerservice.service.FeeScheduleService;
import com.restore.core.service.AppService;
import com.restore.providerservice.dto.FeeSchedule;
import com.restore.providerservice.dto.FeeScheduleResponse;
import com.restore.providerservice.entity.FeeScheduleEntity;
import com.restore.core.entity.ProviderEntity;
import com.restore.providerservice.repository.FeeScheduleRepo;
import com.restore.providerservice.repository.ProviderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FeeScheduleServiceImpl extends AppService implements FeeScheduleService {

    private final FeeScheduleRepo feeScheduleRepo;
    private final ProviderRepo providerRepo;

    @Autowired
    public FeeScheduleServiceImpl(FeeScheduleRepo feeScheduleRepo, ProviderRepo providerRepo) {
        this.feeScheduleRepo = feeScheduleRepo;
        this.providerRepo = providerRepo;
    }

    private FeeScheduleResponse mapToFeeScheduleResponse(FeeScheduleEntity feeScheduleEntity) {
        FeeScheduleResponse feeScheduleResponse = new FeeScheduleResponse();
        feeScheduleResponse.setUuid(feeScheduleEntity.getUuid());
        feeScheduleResponse.setProviderName(feeScheduleEntity.getProvider().getName());
        feeScheduleResponse.setProviderUuid(feeScheduleEntity.getProvider().getUuid());
        feeScheduleResponse.setCptCodeType(feeScheduleEntity.getCptCodeType());
        feeScheduleResponse.setCptCode(feeScheduleEntity.getCptCode());
        feeScheduleResponse.setModifier(feeScheduleEntity.getModifier());
        feeScheduleResponse.setNdcCode(feeScheduleEntity.getNdcCode());
        feeScheduleResponse.setAmount(feeScheduleEntity.getAmount());
        feeScheduleResponse.setNdcQuantity(feeScheduleEntity.getNdcQuantity());
        feeScheduleResponse.setDescription(feeScheduleEntity.getDescription());
        feeScheduleResponse.setActive(feeScheduleEntity.isActive());
        return feeScheduleResponse;
    }

    @Override
    public void addFeeSchedule(FeeSchedule feeSchedule) {
        ProviderEntity providerEntity = providerRepo.findByUuid(feeSchedule.getProviderUuid()).orElseThrow();
        FeeScheduleEntity feeScheduleEntity = new FeeScheduleEntity();
        feeScheduleEntity.setProvider(providerEntity);
        feeScheduleEntity.setCptCodeType(feeSchedule.getCptCodeType());
        feeScheduleEntity.setCptCode(feeSchedule.getCptCode());
        feeScheduleEntity.setModifier(feeSchedule.getModifier());
        feeScheduleEntity.setNdcCode(feeSchedule.getNdcCode());
        feeScheduleEntity.setAmount(feeSchedule.getAmount());
        feeScheduleEntity.setNdcQuantity(feeSchedule.getNdcQuantity());
        feeScheduleEntity.setDescription(feeSchedule.getDescription());
        feeScheduleEntity.setActive(true);
        feeScheduleRepo.save(feeScheduleEntity);
    }

    @Override
    public Page<FeeScheduleResponse> getFeeScheduleList(String cptCode, Boolean active, Pageable pageable) {
        Page<FeeScheduleEntity> feeScheduleEntityList = feeScheduleRepo.findAllByFilter(cptCode, active, pageable);
        return feeScheduleEntityList.map(this::mapToFeeScheduleResponse);
    }

    @Override
    public void changeActiveStatus(UUID feeScheduleUuid, boolean activeStatus) {
        FeeScheduleEntity feeScheduleEntity = feeScheduleRepo.findByUuid(feeScheduleUuid).orElseThrow();
        feeScheduleEntity.setActive(activeStatus);
        feeScheduleRepo.save(feeScheduleEntity);
    }

    @Override
    public void deleteFeeSchedule(UUID feeScheduleUuid) {
        FeeScheduleEntity feeScheduleEntity = feeScheduleRepo.findByUuid(feeScheduleUuid).orElseThrow();
        feeScheduleEntity.setArchive(true);
        feeScheduleRepo.save(feeScheduleEntity);
    }

    @Override
    public FeeScheduleResponse updateFeeSchedule(UUID feeScheduleUuid, FeeSchedule feeSchedule) {
        FeeScheduleEntity feeScheduleEntity = feeScheduleRepo.findByUuid(feeScheduleUuid).orElseThrow();
        feeScheduleEntity.setCptCodeType(feeSchedule.getCptCodeType());
        feeScheduleEntity.setCptCode(feeSchedule.getCptCode());
        feeScheduleEntity.setModifier(feeSchedule.getModifier());
        feeScheduleEntity.setNdcCode(feeSchedule.getNdcCode());
        feeScheduleEntity.setAmount(feeSchedule.getAmount());
        feeScheduleEntity.setNdcQuantity(feeSchedule.getNdcQuantity());
        feeScheduleEntity.setDescription(feeSchedule.getDescription());
        return mapToFeeScheduleResponse(feeScheduleRepo.save(feeScheduleEntity));
    }
}
