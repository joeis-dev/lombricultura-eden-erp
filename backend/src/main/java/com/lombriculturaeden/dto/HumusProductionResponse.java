package com.lombriculturaeden.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HumusProductionResponse {
    private UUID id;
    private LocalDate productionDate;
    private UUID harvestBatchId;
    private String harvestBatchInfo;
    private BigDecimal quantityLiters;
    private BigDecimal ec;
    private BigDecimal ph;
    private BigDecimal ppm;
    private BigDecimal temperature;
    private BigDecimal density;
    private String observations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
