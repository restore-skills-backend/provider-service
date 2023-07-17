package com.restore.providerservice.service;

import com.restore.core.dto.app.Provider;
import com.restore.core.dto.app.ProviderGroup;
import com.restore.core.dto.app.Speciality;
import com.restore.core.exception.RestoreSkillsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.UUID;

public interface ProviderService {
    void add(Provider provider, UUID providerGroupId) throws RestoreSkillsException,IOException;

    Provider get(UUID providerId) throws RestoreSkillsException;
    Provider getByUserId(UUID userId) throws RestoreSkillsException;

    ProviderGroup getProviderGroup() throws RestoreSkillsException,IOException;

    Provider getProfile() throws RestoreSkillsException;

    void updateProviderGroup(ProviderGroup providerGroup) throws RestoreSkillsException, IOException;

    void remove(UUID providerId) throws RestoreSkillsException;

    void update(Provider provider, UUID providerId,String schemaName) throws RestoreSkillsException;

    void updateStatus(UUID providerId, boolean isActive) throws RestoreSkillsException;

    Page<Provider> getAll(Pageable pageable) throws RestoreSkillsException;

    Page<Speciality> getSpecialityList(Pageable pageable);
    // void uploadProfileImage(MultipartFile profileImage, UUID providerId) throws
    // RestoreSkillsException, IOException;
    // String getProviderProfileImage(UUID providerId) throws RestoreSkillsException,
    // IOException;
    // void deleteProviderProfileImage(UUID providerId) throws RestoreSkillsException,
    // IOException;
}
