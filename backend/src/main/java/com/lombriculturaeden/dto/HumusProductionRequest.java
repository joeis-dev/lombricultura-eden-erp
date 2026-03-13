package com.lombriculturaeden.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HumusProductionRequest {
    
    @NotNull(message = "Production date is required")
    private LocalDate productionDate;
    
    private UUID harvestBatchId;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private BigDecimal quantityLiters;
    
    private BigDecimal ec;
    
    private BigDecimal ph;
    
    private BigDecimal ppm;
    
    private BigDecimal temperature;
    
    private BigDecimal density;
    
    private String observations;
}
