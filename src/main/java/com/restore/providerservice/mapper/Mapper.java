package com.restore.providerservice.mapper;

import com.restore.core.dto.app.Provider;
import com.restore.core.dto.app.User;
import com.restore.core.dto.app.enums.Roles;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
public class Mapper  {


    public User mapProviderRequestToUser(Provider providerRequestDTO) {
        return User.builder().email(providerRequestDTO.getEmail())
                .firstName(providerRequestDTO.getName())
                .phone(providerRequestDTO.getPhoneNumber())
                .role(Roles.PROVIDER_ADMIN)
                .build();
    }
}
