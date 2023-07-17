package com.restore.providerservice.dto;

import com.restore.core.entity.LocationHoursEntity;
import com.restore.core.entity.ProviderGroupAddressEntity;
import com.restore.core.entity.SpecialityEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private UUID uuid;
    private String name;
    private String locationId;
    private Set<SpecialityEntity> specialities;
    private String contactNumber;
    private String emailId;
    private String faxId;
    private String information;
    private String avatar;
    private ProviderGroupAddressEntity physicalAddress;
    private ProviderGroupAddressEntity billingAddress;
    private Set<LocationHoursEntity> locationHoursEntities;
    private boolean active;
    private boolean archive;
}
