package com.lombriculturaeden.dto;

import com.lombriculturaeden.entity.WormBedModification;
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
public class WormBedModificationResponse {
    private UUID id;
    private UUID wormBedId;
    private String wormBedName;
    private LocalDate modificationDate;
    private String modificationType;
    private BigDecimal quantity;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static WormBedModificationResponse fromEntity(WormBedModification entity) {
        return WormBedModificationResponse.builder()
                .id(entity.getId())
                .wormBedId(entity.getWormBed().getId())
                .wormBedName(entity.getWormBed().getName())
                .modificationDate(entity.getModificationDate())
                .modificationType(entity.getModificationType())
                .quantity(entity.getQuantity())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
