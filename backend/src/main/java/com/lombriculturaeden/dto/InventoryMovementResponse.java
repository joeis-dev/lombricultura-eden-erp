package com.lombriculturaeden.dto;

import com.lombriculturaeden.entity.InventoryMovement;
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
public class InventoryMovementResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private InventoryMovement.MovementType movementType;
    private BigDecimal quantity;
    private String reason;
    private LocalDateTime movementDate;
    private LocalDateTime createdAt;
    
    public static InventoryMovementResponse fromEntity(InventoryMovement entity) {
        return InventoryMovementResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .movementType(entity.getMovementType())
                .quantity(entity.getQuantity())
                .reason(entity.getReason())
                .movementDate(entity.getMovementDate())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
