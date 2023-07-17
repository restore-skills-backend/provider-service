package com.restore.providerservice.dto;

import com.restore.providerservice.enums.CptCodeType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeSchedule {
    @NotNull
    private UUID providerUuid;
    @NotNull
    private CptCodeType cptCodeType;
    @NotEmpty
    private String cptCode;
    private String modifier;
    private String ndcCode;
    private BigDecimal amount;
    private int ndcQuantity;
    private String description;
}
