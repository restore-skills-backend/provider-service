package com.restore.providerservice.entity;

import com.restore.providerservice.enums.AvailabilityOperationType;
import com.restore.core.dto.app.Base;
import com.restore.core.entity.LocationEntity;
import com.restore.core.entity.ProviderEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "availability_by_date")
public class AvailabilityByDateEntity extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_entity_id")
    private LocationEntity locationEntity;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderEntity providerEntity;

    private AvailabilityOperationType availabilityOperationType;

    @Column(name = "change_date")
    private LocalDate date;
    @Column(name = "start_time")
    private LocalTime start;
    @Column(name = "end_time")
    private LocalTime end;
}
