package com.lombriculturaeden.dto;

import com.lombriculturaeden.entity.HarvestBatch;
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
public class HarvestBatchResponse {
    private UUID id;
    private LocalDate harvestDate;
    private BigDecimal quantityKg;
    private BigDecimal averageWeight;
    private String observations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static HarvestBatchResponse fromEntity(HarvestBatch entity) {
        return HarvestBatchResponse.builder()
                .id(entity.getId())
                .harvestDate(entity.getHarvestDate())
                .quantityKg(entity.getQuantityKg())
                .averageWeight(entity.getAverageWeight())
                .observations(entity.getObservations())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
