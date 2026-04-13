package com.lombriculturaeden.service;

import com.lombriculturaeden.dto.WormBedModificationRequest;
import com.lombriculturaeden.dto.WormBedModificationResponse;
import com.lombriculturaeden.entity.WormBed;
import com.lombriculturaeden.entity.WormBedModification;
import com.lombriculturaeden.exception.ResourceNotFoundException;
import com.lombriculturaeden.repository.WormBedModificationRepository;
import com.lombriculturaeden.repository.WormBedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WormBedModificationService {
    
    private final WormBedModificationRepository repository;
    private final WormBedRepository wormBedRepository;
    private final WormBedService wormBedService;
    
    @Transactional(readOnly = true)
    public List<WormBedModificationResponse> findAll() {
        return repository.findAllByOrderByModificationDateDesc().stream()
                .map(WormBedModificationResponse::fromEntity)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<WormBedModificationResponse> findByWormBedId(UUID wormBedId) {
        return repository.findByWormBedIdOrderByModificationDateDesc(wormBedId).stream()
                .map(WormBedModificationResponse::fromEntity)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public WormBedModificationResponse findById(UUID id) {
        return repository.findById(id)
                .map(WormBedModificationResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Worm bed modification not found: " + id));
    }
    
    @Transactional
    public WormBedModificationResponse create(WormBedModificationRequest request) {
        WormBed wormBed = wormBedRepository.findById(request.getWormBedId())
                .orElseThrow(() -> new ResourceNotFoundException("Worm bed not found: " + request.getWormBedId()));
        
        if ("ADDITION".equals(request.getModificationType())) {
            if (request.getQuantity() != null && wormBed.getApproximateCount() != null) {
                wormBed.setApproximateCount(wormBed.getApproximateCount().add(request.getQuantity()));
            } else if (request.getQuantity() != null) {
                wormBed.setApproximateCount(request.getQuantity());
            }
            wormBedRepository.save(wormBed);
        } else if ("SUBSTRACTION".equals(request.getModificationType())) {
            if (wormBed.getApproximateCount() != null && request.getQuantity() != null) {
                wormBed.setApproximateCount(wormBed.getApproximateCount().subtract(request.getQuantity()));
            }
            wormBed.setLastSubtraction(request.getModificationDate());
            wormBedRepository.save(wormBed);
        }
        
        WormBedModification entity = WormBedModification.builder()
                .wormBed(wormBed)
                .modificationDate(request.getModificationDate())
                .modificationType(request.getModificationType())
                .quantity(request.getQuantity())
                .description(request.getDescription())
                .build();
        
        return WormBedModificationResponse.fromEntity(repository.save(entity));
    }
    
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Worm bed modification not found: " + id);
        }
        repository.deleteById(id);
    }
}
