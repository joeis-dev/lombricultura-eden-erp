package com.lombriculturaeden.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "worm_beds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WormBed extends BaseEntity {
    
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    
    @Column(name = "approximate_count", precision = 10, scale = 2)
    private BigDecimal approximateCount;
    
    @Column(name = "length_meters", precision = 10, scale = 2)
    private BigDecimal lengthMeters;
    
    @Column(name = "width_meters", precision = 10, scale = 2)
    private BigDecimal widthMeters;
    
    @Column(name = "height_meters", precision = 10, scale = 2)
    private BigDecimal heightMeters;
    
    @Column(name = "last_feed")
    private LocalDate lastFeed;
    
    @Column(name = "last_subtraction")
    private LocalDate lastSubtraction;
    
    @Column(name = "modification_cooldown_days")
    @Builder.Default
    private Integer modificationCooldownDays = 21;
    
    @Column(columnDefinition = "TEXT")
    private String observations;
    
    @Column(nullable = false)
    @Builder.Default
    private String status = "ACTIVE";
}
