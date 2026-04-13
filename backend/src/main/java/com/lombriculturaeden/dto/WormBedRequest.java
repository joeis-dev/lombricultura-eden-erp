package com.lombriculturaeden.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WormBedRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private BigDecimal approximateCount;
    
    @PositiveOrZero(message = "Length must be zero or positive")
    private BigDecimal lengthMeters;
    
    @PositiveOrZero(message = "Width must be zero or positive")
    private BigDecimal widthMeters;
    
    @PositiveOrZero(message = "Height must be zero or positive")
    private BigDecimal heightMeters;
    
    private LocalDate lastFeed;
    
    private LocalDate lastSubtraction;
    
    @Positive(message = "Modification cooldown must be positive")
    private Integer modificationCooldownDays;
    
    private String observations;
}
