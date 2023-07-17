package com.restore.providerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityByDateResponse {
    private LocalDate date;
    private Set<TimeLog> timeLogSet;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeLog {
        private LocalTime startTime;
        private LocalTime endTime;
    }
}
