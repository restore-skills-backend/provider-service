package com.restore.providerservice.service;

import com.restore.core.exception.RestoreSkillsException;
import com.restore.providerservice.dto.Availability;
import com.restore.providerservice.dto.AvailabilityByDate;
import com.restore.providerservice.dto.AvailabilityByDateResponse;
import com.restore.providerservice.entity.AvailabilityByDateEntity;
import com.restore.providerservice.entity.AvailabilityEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AvailabilityService {
    void addAvailability(Availability availability) throws RestoreSkillsException;

    void setBookingWindowDates(Availability availability, AvailabilityEntity availabilityEntity) throws RestoreSkillsException;

    AvailabilityEntity getByLocationUuid(UUID location_uuid) throws RestoreSkillsException;

    void editAvailability(UUID availability_uuid, Availability availability) throws RestoreSkillsException;

    AvailabilityByDateEntity addAvailabilityByDate(AvailabilityByDate availability) throws RestoreSkillsException;

    List<AvailabilityByDateResponse> getAvailabilityByDateByLocationUuid(UUID locationUuid, LocalDate startDate) throws RestoreSkillsException;
}
