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
public class WormBedModificationRequest {
    
    @NotNull(message = "Worm bed ID is required")
    private UUID wormBedId;
    
    @NotNull(message = "Modification date is required")
    private LocalDate modificationDate;
    
    @NotNull(message = "Modification type is required")
    private String modificationType;
    
    private BigDecimal quantity;
    
    private String description;
}
