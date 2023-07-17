package com.restore.providerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockDay {
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate toDate;
}
