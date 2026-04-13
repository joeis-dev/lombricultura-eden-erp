package com.lombriculturaeden.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "worm_bed_modifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WormBedModification extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worm_bed_id", nullable = false)
    private WormBed wormBed;
    
    @Column(name = "modification_date", nullable = false)
    private LocalDate modificationDate;
    
    @Column(name = "modification_type", nullable = false)
    private String modificationType;
    
    @Column(name = "quantity", precision = 10, scale = 2)
    private BigDecimal quantity;
    
    @Column(columnDefinition = "TEXT")
    private String description;
}
