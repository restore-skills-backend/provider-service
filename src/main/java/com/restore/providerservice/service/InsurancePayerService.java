package com.restore.providerservice.service;

import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.dto.app.InsurancePayer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InsurancePayerService {
    Page<InsurancePayer> getAll(Pageable pageable) throws RestoreSkillsException;
}
