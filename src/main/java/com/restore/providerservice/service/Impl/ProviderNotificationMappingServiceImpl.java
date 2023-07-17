package com.restore.providerservice.service.Impl;

import com.restore.providerservice.service.ProviderNotificationMappingService;
import com.restore.core.dto.app.User;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.entity.UserEntity;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import com.restore.providerservice.dto.ProviderNotificationMapping;
import com.restore.providerservice.dto.ProviderNotificationResponse;
import com.restore.providerservice.entity.NotificationTypeEntity;
import com.restore.core.entity.ProviderEntity;
import com.restore.providerservice.entity.ProviderNotificationMappingEntity;
import com.restore.providerservice.repository.NotificationTypeRepo;
import com.restore.providerservice.repository.ProviderNotificationMappingRepo;
import com.restore.providerservice.repository.ProviderRepo;
import com.restore.providerservice.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProviderNotificationMappingServiceImpl extends AppService implements ProviderNotificationMappingService {

    private final NotificationTypeRepo notificationTypeRepo;
    private final UserRepo userRepo;
    private final ProviderRepo providerRepository;
    private final ProviderNotificationMappingRepo providerNotificationMappingRepo;

    @Autowired
    public ProviderNotificationMappingServiceImpl(NotificationTypeRepo notificationTypeRepo, UserRepo userRepo, ProviderRepo providerRepository, ProviderNotificationMappingRepo providerNotificationMappingRepo) {
        this.notificationTypeRepo = notificationTypeRepo;
        this.userRepo = userRepo;
        this.providerRepository = providerRepository;
        this.providerNotificationMappingRepo = providerNotificationMappingRepo;
    }

    private ProviderNotificationResponse mapProviderNotificationResponseEntities(NotificationTypeEntity notificationTypeEntity, ProviderEntity providerEntity) {
        Optional<ProviderNotificationMappingEntity> providerNotificationMappingEntityOptional = providerNotificationMappingRepo.findByProviderEntityAndNotificationTypeEntity(providerEntity, notificationTypeEntity);
        ProviderNotificationResponse providerNotificationResponse = new ProviderNotificationResponse();
        providerNotificationResponse.setNotificationId(notificationTypeEntity.getId());
        providerNotificationResponse.setType(notificationTypeEntity.getType());
        if (providerNotificationMappingEntityOptional.isPresent()) {
            providerNotificationResponse.setAllowEmail(providerNotificationMappingEntityOptional.get().isAllowEmail());
            providerNotificationResponse.setAllowPush(providerNotificationMappingEntityOptional.get().isAllowPush());
            providerNotificationResponse.setAllowText(providerNotificationMappingEntityOptional.get().isAllowText());
        }
        return providerNotificationResponse;
    }

    private ProviderEntity getCurrentProvider() throws RestoreSkillsException {
        User user = getCurrentUser();
        Optional<UserEntity> userEntity = userRepo.findByEmail(user.getEmail());
        if(userEntity.isPresent()) {
            return providerRepository.findByUserId(userEntity.get());
        }
        else {
            throwError(ResponseCode.IAM_ERROR, "Access denied! Logged in user does not exists");
            return null;
        }
    }


    @Override
    public void saveOrUpdateProviderNotification(ProviderNotificationMapping providerNotificationMapping) throws RestoreSkillsException {
        ProviderEntity providerEntity = getCurrentProvider();
        Set<ProviderNotificationMapping.ProviderNotificationMappingData> providerNotificationMappingDataSet = providerNotificationMapping.getProviderNotificationMappingList();
        providerNotificationMappingDataSet.forEach(providerNotificationMappingData -> {
            Optional<NotificationTypeEntity> notificationTypeEntity = notificationTypeRepo.findById(providerNotificationMappingData.getNotificationTypeId());
            if(notificationTypeEntity.isPresent()) {
                Optional<ProviderNotificationMappingEntity> providerNotificationMappingEntity = providerNotificationMappingRepo.findByProviderEntityAndNotificationTypeEntity(providerEntity, notificationTypeEntity.get());
                if(providerNotificationMappingEntity.isPresent()) {
                    providerNotificationMappingEntity.get().setAllowEmail(providerNotificationMappingData.isAllowEmail());
                    providerNotificationMappingEntity.get().setAllowPush(providerNotificationMappingData.isAllowPush());
                    providerNotificationMappingEntity.get().setAllowText(providerNotificationMappingData.isAllowText());
                    providerNotificationMappingRepo.save(providerNotificationMappingEntity.get());
                }
                else {
                    ProviderNotificationMappingEntity newProviderNotificationMappingEntity = new ProviderNotificationMappingEntity();
                    newProviderNotificationMappingEntity.setProviderEntity(providerEntity);
                    newProviderNotificationMappingEntity.setNotificationTypeEntity(notificationTypeEntity.get());
                    newProviderNotificationMappingEntity.setAllowEmail(providerNotificationMappingData.isAllowEmail());
                    newProviderNotificationMappingEntity.setAllowPush(providerNotificationMappingData.isAllowPush());
                    newProviderNotificationMappingEntity.setAllowText(providerNotificationMappingData.isAllowText());
                    providerNotificationMappingRepo.save(newProviderNotificationMappingEntity);
                }
            }
        });
    }

    @Override
    public List<ProviderNotificationResponse> getNotificationSettingsList() throws RestoreSkillsException {
        ProviderEntity providerEntity = getCurrentProvider();
        return notificationTypeRepo.findAll().stream()
                .map(notificationEntity -> mapProviderNotificationResponseEntities(notificationEntity, providerEntity))
                .toList();
    }
}
