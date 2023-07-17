package com.restore.providerservice.service.Impl;

import com.restore.providerservice.service.InsurancePayerService;
import com.restore.core.service.AppService;
import com.restore.core.dto.app.InsurancePayer;
import com.restore.core.entity.InsurancePayerEntity;
import com.restore.providerservice.repository.InsurancePayerRepo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InsurancePayerImpl extends AppService implements InsurancePayerService {
    private final InsurancePayerRepo insurancePayerRepo;
    private final ModelMapper modelMapper;

    @Autowired
    public InsurancePayerImpl(InsurancePayerRepo insurancePayerRepo, ModelMapper modelMapper) {
        this.insurancePayerRepo = insurancePayerRepo;
        this.modelMapper = modelMapper;
    }

    private InsurancePayer toInsurance(InsurancePayerEntity insurancePayerEntity){
        return modelMapper.map(insurancePayerEntity, InsurancePayer.class);
    }
    @Override
    public Page<InsurancePayer> getAll(Pageable pageable){
        Page<InsurancePayerEntity> insurancePayers = insurancePayerRepo.findAll(pageable);
        return insurancePayers.map(this::toInsurance);
    }
}
