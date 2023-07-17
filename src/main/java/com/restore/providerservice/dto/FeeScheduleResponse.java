package com.restore.providerservice.dto;

import com.restore.providerservice.enums.CptCodeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeScheduleResponse {
    private UUID uuid;
    private String providerName;
    private UUID providerUuid;
    private CptCodeType cptCodeType;
    private String cptCode;
    private String modifier;
    private String ndcCode;
    private BigDecimal amount;
    private int ndcQuantity;
    private String description;
    private boolean active;
}
