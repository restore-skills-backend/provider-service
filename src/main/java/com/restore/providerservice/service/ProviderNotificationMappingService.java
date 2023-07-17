package com.restore.providerservice.service;

import com.restore.core.exception.RestoreSkillsException;
import com.restore.providerservice.dto.ProviderNotificationMapping;
import com.restore.providerservice.dto.ProviderNotificationResponse;

import java.util.List;

public interface ProviderNotificationMappingService {
    void saveOrUpdateProviderNotification(ProviderNotificationMapping providerNotificationMapping) throws RestoreSkillsException;
    List<ProviderNotificationResponse> getNotificationSettingsList() throws RestoreSkillsException;
}
