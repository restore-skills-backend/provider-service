package com.restore.providerservice.entity;

import com.restore.core.dto.app.Base;
import com.restore.core.entity.ProviderEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "provider_notification_mapping")
public class ProviderNotificationMappingEntity extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderEntity providerEntity;

    @ManyToOne
    @JoinColumn(name = "notification_type_id")
    private NotificationTypeEntity notificationTypeEntity;

    private boolean allowPush;
    private boolean allowText;
    private boolean allowEmail;
}