package com.restore.providerservice.entity;

import com.restore.providerservice.enums.CptCodeType;
import com.restore.core.dto.app.Base;
import com.restore.core.entity.ProviderEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fee_schedule")
public class FeeScheduleEntity extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID uuid = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderEntity provider;

    @Column(name = "cpt_code_type")
    private CptCodeType cptCodeType;

    private String cptCode;
    private String modifier;
    private String ndcCode;
    private BigDecimal amount;
    private int ndcQuantity;
    private String description;
    private boolean active;
    private boolean archive;
}
