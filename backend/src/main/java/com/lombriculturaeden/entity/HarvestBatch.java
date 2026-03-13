package com.lombriculturaeden.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "harvest_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarvestBatch extends BaseEntity {
    
    @Column(name = "harvest_date", nullable = false)
    private LocalDate harvestDate;
    
    @Column(name = "quantity_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityKg;
    
    @Column(name = "average_weight", precision = 10, scale = 2)
    private BigDecimal averageWeight;
    
    @Column(columnDefinition = "TEXT")
    private String observations;
}
