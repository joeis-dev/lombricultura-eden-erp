package com.lombriculturaeden.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "humus_productions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HumusProduction extends BaseEntity {
    
    @Column(name = "production_date", nullable = false)
    private LocalDate productionDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "harvest_batch_id")
    private HarvestBatch harvestBatch;
    
    @Column(name = "quantity_liters", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityLiters;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal ec;
    
    @Column(precision = 4, scale = 2)
    private BigDecimal ph;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal ppm;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal temperature;
    
    @Column(precision = 5, scale = 3)
    private BigDecimal density;
    
    @Column(columnDefinition = "TEXT")
    private String observations;
}
