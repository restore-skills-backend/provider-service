package com.restore.providerservice.service;

import com.restore.core.dto.app.CountryState;
import com.restore.core.exception.RestoreSkillsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CountryStateService {
    Page<CountryState> getAll(Pageable pageable) throws RestoreSkillsException;
}
