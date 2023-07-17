package com.restore.providerservice.controller;

import com.restore.providerservice.service.AvailabilityService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.response.Response;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.providerservice.dto.Availability;
import com.restore.providerservice.dto.AvailabilityByDate;
import com.restore.providerservice.entity.AvailabilityEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("api/provider/availability")
public class AvailabilityController extends AppController {

    private final AvailabilityService availabilityService;

    @Autowired
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public ResponseEntity<Response> save(@RequestBody @Valid Availability availability) throws RestoreSkillsException {
        availabilityService.addAvailability(availability);
        return data("Availability added successfully!");
    }

    @GetMapping("/{location_uuid}")
    public ResponseEntity<Response> getByLocationUuid(@PathVariable UUID location_uuid) throws RestoreSkillsException {
        AvailabilityEntity availabilityEntity;
        try {
            availabilityEntity = availabilityService.getByLocationUuid(location_uuid);
        }
        catch (Exception exception) {
            throw exception;
        }
        return data(availabilityEntity);
    }

    @PutMapping("/{availability_uuid}")
    public ResponseEntity<Response> editAvailability(@PathVariable UUID availability_uuid, @RequestBody @Valid Availability availability) throws RestoreSkillsException {
        availabilityService.editAvailability(availability_uuid, availability);
        return data("Availability updated successfully!");
    }

    @PostMapping("/by_date")
    public ResponseEntity<Response> addAvailabilityByDate(@RequestBody @Valid AvailabilityByDate availability) throws RestoreSkillsException {
        return data(availabilityService.addAvailabilityByDate(availability));
    }

    @GetMapping
    public ResponseEntity<Response> getAvailabilitiesByDate(@RequestParam @NotNull UUID locationUuid, @RequestParam @NotNull LocalDate startDate) throws RestoreSkillsException {
        return data(availabilityService.getAvailabilityByDateByLocationUuid(locationUuid, startDate));
    }

}
