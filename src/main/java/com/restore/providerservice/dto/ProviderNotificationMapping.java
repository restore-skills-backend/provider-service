package com.restore.providerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderNotificationMapping {

    private Set<ProviderNotificationMappingData> providerNotificationMappingList;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderNotificationMappingData {
        @NotNull
        private Long notificationTypeId;
        private boolean allowPush;
        private boolean allowText;
        private boolean allowEmail;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProviderNotificationMappingData that = (ProviderNotificationMappingData) o;
            return notificationTypeId == that.notificationTypeId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(notificationTypeId);
        }
    }
}
