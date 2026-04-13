package com.lombriculturaeden.service;

import com.lombriculturaeden.dto.WormBedRequest;
import com.lombriculturaeden.dto.WormBedResponse;
import com.lombriculturaeden.entity.WormBed;
import com.lombriculturaeden.exception.ResourceNotFoundException;
import com.lombriculturaeden.repository.WormBedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WormBedService {
    
    private final WormBedRepository repository;
    
    @Transactional(readOnly = true)
    public List<WormBedResponse> findAll() {
        return repository.findAllByOrderByNameAsc().stream()
                .map(WormBedResponse::fromEntity)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public WormBedResponse findById(UUID id) {
        return repository.findById(id)
                .map(WormBedResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Worm bed not found: " + id));
    }
    
    @Transactional
    public WormBedResponse create(WormBedRequest request) {
        if (repository.existsByName(request.getName())) {
            throw new IllegalArgumentException("A worm bed with this name already exists");
        }
        
        WormBed entity = WormBed.builder()
                .name(request.getName())
                .approximateCount(request.getApproximateCount())
                .lengthMeters(request.getLengthMeters())
                .widthMeters(request.getWidthMeters())
                .heightMeters(request.getHeightMeters())
                .lastFeed(request.getLastFeed())
                .lastSubtraction(request.getLastSubtraction())
                .modificationCooldownDays(request.getModificationCooldownDays() != null ? request.getModificationCooldownDays() : 21)
                .observations(request.getObservations())
                .build();
        
        return WormBedResponse.fromEntity(repository.save(entity));
    }
    
    @Transactional
    public WormBedResponse update(UUID id, WormBedRequest request) {
        WormBed entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worm bed not found: " + id));
        
        if (!entity.getName().equals(request.getName()) && repository.existsByName(request.getName())) {
            throw new IllegalArgumentException("A worm bed with this name already exists");
        }
        
        entity.setName(request.getName());
        entity.setApproximateCount(request.getApproximateCount());
        entity.setLengthMeters(request.getLengthMeters());
        entity.setWidthMeters(request.getWidthMeters());
        entity.setHeightMeters(request.getHeightMeters());
        entity.setLastFeed(request.getLastFeed());
        entity.setLastSubtraction(request.getLastSubtraction());
        if (request.getModificationCooldownDays() != null) {
            entity.setModificationCooldownDays(request.getModificationCooldownDays());
        }
        entity.setObservations(request.getObservations());
        
        return WormBedResponse.fromEntity(repository.save(entity));
    }
    
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Worm bed not found: " + id);
        }
        repository.deleteById(id);
    }
    
    @Transactional
    public WormBedResponse updateSubtractionDate(UUID id, java.time.LocalDate date) {
        WormBed entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worm bed not found: " + id));
        
        entity.setLastSubtraction(date);
        return WormBedResponse.fromEntity(repository.save(entity));
    }
    
    @Transactional
    public WormBedResponse updateApproximateCount(UUID id, java.math.BigDecimal count) {
        WormBed entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worm bed not found: " + id));
        
        entity.setApproximateCount(count);
        return WormBedResponse.fromEntity(repository.save(entity));
    }
}
