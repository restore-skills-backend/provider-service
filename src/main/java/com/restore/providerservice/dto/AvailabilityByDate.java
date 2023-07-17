package com.restore.providerservice.dto;

import com.restore.providerservice.enums.AvailabilityOperationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityByDate {

    private Long id;
    @NotNull
    private UUID locationUuid;
    @NotNull
    private AvailabilityOperationType availabilityOperationType;
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime start;
    @NotNull
    private LocalTime end;
}
