package com.restore.providerservice.controller;

import com.restore.providerservice.service.LocationService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.app.Location;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/provider/location")
@Slf4j
public class ProviderGroupLocationController extends AppController {

    private final LocationService locationService;

    @Autowired
    public ProviderGroupLocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping()
    public ResponseEntity<Response> getAllLocations() throws IOException {
        return data(locationService.getLocationList());
    }

    @PostMapping("/provider-group")
    public ResponseEntity<Response> addLocation(@RequestBody @Valid Location location) throws RestoreSkillsException {
        locationService.addLocation(location);
        return success(ResponseCode.OK,"Location group is added successfully");
    }

    @GetMapping("/provider-group")
    public ResponseEntity<Response> getAllLocationsByProvider(Pageable page) throws RestoreSkillsException {
        return data(locationService.getAllLocationsByProvider(page));
    }

    @PutMapping("/provider-group/location-id/{locationId}/active/{active}")
    public ResponseEntity<Response> updateLocationStatus(@PathVariable("locationId") Long locationId, @PathVariable("active") boolean active) throws RestoreSkillsException {
        locationService.updateStatus(locationId,active);
        return success(ResponseCode.OK,"Status updated successfully");
    }

    @GetMapping("/provider-group/location-id/{locationId}")
    public ResponseEntity<Response> getLocationByLocationId(@PathVariable("locationId") Long locationId) throws RestoreSkillsException {
        return data(ResponseCode.OK,"Location found successfully",locationService.getLocationByLocationId(locationId));
    }
}
