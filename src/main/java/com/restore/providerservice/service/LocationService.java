package com.restore.providerservice.service;

import com.restore.core.exception.RestoreSkillsException;
import com.restore.providerservice.dto.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface LocationService {
    List<Location> getLocationList() throws IOException;

    void addLocation(com.restore.core.dto.app.Location location) throws RestoreSkillsException;

    Page<com.restore.core.dto.app.Location> getAllLocationsByProvider(Pageable page) throws RestoreSkillsException;

    void updateLocation(Long id, com.restore.core.dto.app.Location location) throws RestoreSkillsException, IOException;

    void updateStatus(Long locationId, boolean active) throws RestoreSkillsException;

    Location getLocationByLocationId(Long locationId) throws RestoreSkillsException;
}
