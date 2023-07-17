package com.restore.providerservice.controller;

import com.restore.providerservice.service.ProviderNotificationMappingService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.providerservice.dto.ProviderNotificationMapping;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/provider/notification")
public class ProviderNotificationMappingController extends AppController {

    private final ProviderNotificationMappingService providerNotificationMappingService;

    @Autowired
    public ProviderNotificationMappingController(ProviderNotificationMappingService providerNotificationMappingService) {
        this.providerNotificationMappingService = providerNotificationMappingService;
    }

    @PostMapping()
    public ResponseEntity<Response> saveOrUpdateProviderNotification(@RequestBody @Valid ProviderNotificationMapping providerNotificationMapping) throws RestoreSkillsException {
        providerNotificationMappingService.saveOrUpdateProviderNotification(providerNotificationMapping);
        return success(ResponseCode.OK, "Successfully updated Notification configurations for provider");
    }

    @GetMapping("/list")
    public ResponseEntity<Response> getNotificationSettingsList() throws RestoreSkillsException {
        return data(providerNotificationMappingService.getNotificationSettingsList());
    }
}
