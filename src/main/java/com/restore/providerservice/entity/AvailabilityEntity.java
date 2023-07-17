package com.restore.providerservice.entity;

import com.restore.providerservice.enums.BookingWindow;
import com.restore.providerservice.enums.BookingWindowTimeZone;
import com.restore.providerservice.enums.SchedulingNoticeInputType;
import com.restore.core.dto.app.Base;
import com.restore.core.entity.LocationEntity;
import com.restore.core.entity.ProviderEntity;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Table(name = "availability")
public class AvailabilityEntity extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "availability_id", referencedColumnName = "id")
    private Set<DayWiseAvailabilityEntity> dayWiseAvailabilityEntitySet;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "availability_id", referencedColumnName = "id")
    private Set<BlockDayEntity> blockDayEntitySet;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity providerEntity;

    @OneToOne
    @Unique
    @JoinColumn(name = "location_entity_id")
    private LocationEntity locationEntity;
    private UUID uuid;
    private int initialConsultTime;
    private int followUpConsultTime;
    private int minScheduleNoticeInput;
    private SchedulingNoticeInputType schedulingNoticeInputType;
    private int eventBuffer;
    private BookingWindow bookingWindow;
    private BookingWindowTimeZone bookingWindowTimeZone;
    private LocalDate bookingWindowStart;
    private LocalDate bookingWindowEnd;

}
