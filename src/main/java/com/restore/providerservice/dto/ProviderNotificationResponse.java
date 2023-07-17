package com.restore.providerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderNotificationResponse {
    private long notificationId;
    private String type;
    private boolean allowPush;
    private boolean allowText;
    private boolean allowEmail;
}
