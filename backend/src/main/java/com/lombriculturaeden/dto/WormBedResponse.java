package com.lombriculturaeden.dto;

import com.lombriculturaeden.entity.WormBed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WormBedResponse {
    private UUID id;
    private String name;
    private BigDecimal approximateCount;
    private BigDecimal lengthMeters;
    private BigDecimal widthMeters;
    private BigDecimal heightMeters;
    private BigDecimal volumeCubicMeters;
    private Long estimatedPopulation;
    private String populationEstimateBasis;
    private LocalDate lastFeed;
    private LocalDate lastSubtraction;
    private Integer modificationCooldownDays;
    private String observations;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean canModify;
    private Long daysUntilNextModification;
    
    private static final double WORMS_PER_CUBIC_METER = 500.0;
    private static final double OPTIMAL_DENSITY_FACTOR = 1.5;
    private static final double REPRODUCTION_CYCLE_DAYS = 60.0;
    private static final double MAX_REPRODUCTION_MULTIPLIER = 2.5;
    private static final int DAYS_PER_FEEDING_OPTIMAL = 7;
    
    public static WormBedResponse fromEntity(WormBed entity) {
        WormBedResponse response = WormBedResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .approximateCount(entity.getApproximateCount())
                .lengthMeters(entity.getLengthMeters())
                .widthMeters(entity.getWidthMeters())
                .heightMeters(entity.getHeightMeters())
                .lastFeed(entity.getLastFeed())
                .lastSubtraction(entity.getLastSubtraction())
                .modificationCooldownDays(entity.getModificationCooldownDays())
                .observations(entity.getObservations())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        
        calculatePopulationEstimate(response, entity);
        
        int cooldownDays = entity.getModificationCooldownDays() != null ? entity.getModificationCooldownDays() : 21;
        
        if (entity.getLastSubtraction() != null) {
            long daysSinceSubtraction = ChronoUnit.DAYS.between(entity.getLastSubtraction(), LocalDate.now());
            long daysUntil = cooldownDays - daysSinceSubtraction;
            response.setDaysUntilNextModification(Math.max(0, daysUntil));
            response.setCanModify(daysSinceSubtraction >= cooldownDays);
        } else {
            response.setDaysUntilNextModification(0L);
            response.setCanModify(true);
        }
        
        return response;
    }
    
    private static void calculatePopulationEstimate(WormBedResponse response, WormBed entity) {
        BigDecimal length = entity.getLengthMeters();
        BigDecimal width = entity.getWidthMeters();
        BigDecimal height = entity.getHeightMeters();
        
        if (length == null || width == null || height == null ||
            length.compareTo(BigDecimal.ZERO) <= 0 ||
            width.compareTo(BigDecimal.ZERO) <= 0 ||
            height.compareTo(BigDecimal.ZERO) <= 0) {
            response.setVolumeCubicMeters(null);
            response.setEstimatedPopulation(null);
            response.setPopulationEstimateBasis("Se requieren dimensiones del lecho para estimar población");
            return;
        }
        
        BigDecimal volume = length.multiply(width).multiply(height).setScale(2, RoundingMode.HALF_UP);
        response.setVolumeCubicMeters(volume);
        
        long basePopulation = (long) (volume.doubleValue() * WORMS_PER_CUBIC_METER);
        
        double reproductionFactor = calculateReproductionFactor(entity);
        double feedingFactor = calculateFeedingFactor(entity);
        
        long estimatedPopulation = (long) (basePopulation * reproductionFactor * feedingFactor);
        
        estimatedPopulation = Math.min(estimatedPopulation, (long) (basePopulation * MAX_REPRODUCTION_MULTIPLIER));
        
        response.setEstimatedPopulation(estimatedPopulation);
        
        String basis = String.format(
            "Base: %.0f worms/m³ × %.2f m³ = %d | Reproducción: ×%.2f | Alimentación: ×%.2f",
            WORMS_PER_CUBIC_METER,
            volume.doubleValue(),
            basePopulation,
            reproductionFactor,
            feedingFactor
        );
        response.setPopulationEstimateBasis(basis);
    }
    
    private static double calculateReproductionFactor(WormBed entity) {
        LocalDate lastSubtraction = entity.getLastSubtraction();
        if (lastSubtraction == null) {
            return OPTIMAL_DENSITY_FACTOR;
        }
        
        long daysSinceSubtraction = ChronoUnit.DAYS.between(lastSubtraction, LocalDate.now());
        
        if (daysSinceSubtraction >= REPRODUCTION_CYCLE_DAYS) {
            return OPTIMAL_DENSITY_FACTOR;
        }
        
        double progression = daysSinceSubtraction / REPRODUCTION_CYCLE_DAYS;
        return 1.0 + (OPTIMAL_DENSITY_FACTOR - 1.0) * progression;
    }
    
    private static double calculateFeedingFactor(WormBed entity) {
        LocalDate lastFeed = entity.getLastFeed();
        if (lastFeed == null) {
            return 0.8;
        }
        
        long daysSinceFeeding = ChronoUnit.DAYS.between(lastFeed, LocalDate.now());
        
        if (daysSinceFeeding <= DAYS_PER_FEEDING_OPTIMAL) {
            return 1.2;
        } else if (daysSinceFeeding <= DAYS_PER_FEEDING_OPTIMAL * 2) {
            return 1.0;
        } else if (daysSinceFeeding <= DAYS_PER_FEEDING_OPTIMAL * 3) {
            return 0.9;
        } else if (daysSinceFeeding <= DAYS_PER_FEEDING_OPTIMAL * 4) {
            return 0.7;
        } else {
            return 0.5;
        }
    }
}
