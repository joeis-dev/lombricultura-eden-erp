package com.lombriculturaeden.dto;

import com.lombriculturaeden.entity.HarvestBatch;
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
public class HarvestBatchRequest {
    
    @NotNull(message = "Harvest date is required")
    private LocalDate harvestDate;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private BigDecimal quantityKg;
    
    private BigDecimal averageWeight;
    
    private String observations;
}
