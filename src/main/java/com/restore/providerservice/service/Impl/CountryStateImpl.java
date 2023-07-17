package com.restore.providerservice.service.Impl;

import com.restore.providerservice.service.CountryStateService;
import com.restore.core.dto.app.CountryState;
import com.restore.core.service.AppService;
import com.restore.core.entity.CountryStateEntity;
import com.restore.providerservice.repository.CountryStateRepo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CountryStateImpl extends AppService implements CountryStateService {

    private final CountryStateRepo countryStateRepo;
    private final ModelMapper modelMapper;
    @Autowired
    public CountryStateImpl(CountryStateRepo countryStateRepo, ModelMapper modelMapper) {
        this.countryStateRepo = countryStateRepo;
        this.modelMapper = modelMapper;
    }

    private CountryState toCountryState(CountryStateEntity countryStateEntity){
        return modelMapper.map(countryStateEntity, CountryState.class);
    }
    @Override
    public Page<CountryState> getAll(Pageable pageable){
        Page<CountryStateEntity> countryStateEntities = countryStateRepo.findAll(pageable);
        return countryStateEntities.map(this::toCountryState);
    }
}
