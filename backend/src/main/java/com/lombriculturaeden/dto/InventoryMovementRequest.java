package com.lombriculturaeden.dto;

import com.lombriculturaeden.entity.InventoryMovement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementRequest {
    
    @NotNull(message = "Product is required")
    private UUID productId;
    
    @NotNull(message = "Movement type is required")
    private InventoryMovement.MovementType movementType;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private BigDecimal quantity;
    
    private String reason;
    
    private LocalDateTime movementDate;
}
