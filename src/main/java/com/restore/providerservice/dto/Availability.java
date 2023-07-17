package com.restore.providerservice.dto;

import com.restore.providerservice.validator.ValidateAvailability;
import com.restore.core.entity.LocationEntity;
import com.restore.core.entity.ProviderEntity;
import com.restore.providerservice.enums.BookingWindow;
import com.restore.providerservice.enums.BookingWindowTimeZone;
import com.restore.providerservice.enums.SchedulingNoticeInputType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidateAvailability
public class Availability {

    private Set<DayWiseAvailability> dayWiseAvailabilityEntitySet;
    private Set<BlockDay> blockDayEntitySet;
    @NotNull
    private UUID locationUuid;
    private Integer initialConsultTime;
    private Integer followUpConsultTime;
    private Integer minScheduleNoticeInput;
    private SchedulingNoticeInputType schedulingNoticeInputType;
    private int eventBuffer;
    @NotNull
    private BookingWindow bookingWindow;
    @NotNull
    private BookingWindowTimeZone bookingWindowTimeZone;
    @Transient
    private ProviderEntity providerEntity;
    @Transient
    private LocationEntity locationEntity;

}
