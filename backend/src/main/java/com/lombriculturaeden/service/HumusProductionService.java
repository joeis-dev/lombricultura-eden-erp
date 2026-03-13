package com.lombriculturaeden.service;

import com.lombriculturaeden.dto.HumusProductionRequest;
import com.lombriculturaeden.dto.HumusProductionResponse;
import com.lombriculturaeden.entity.HarvestBatch;
import com.lombriculturaeden.entity.HumusProduction;
import com.lombriculturaeden.exception.ResourceNotFoundException;
import com.lombriculturaeden.repository.HarvestBatchRepository;
import com.lombriculturaeden.repository.HumusProductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HumusProductionService {
    
    private final HumusProductionRepository repository;
    private final HarvestBatchRepository harvestBatchRepository;
    
    @Transactional(readOnly = true)
    public List<HumusProductionResponse> findAll() {
        return repository.findAllByOrderByProductionDateDesc().stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public HumusProductionResponse findById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Humus production not found: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<HumusProductionResponse> findByBatch(UUID batchId) {
        return repository.findByHarvestBatchId(batchId).stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional
    public HumusProductionResponse create(HumusProductionRequest request) {
        HumusProduction entity = HumusProduction.builder()
                .productionDate(request.getProductionDate())
                .quantityLiters(request.getQuantityLiters())
                .ec(request.getEc())
                .ph(request.getPh())
                .ppm(request.getPpm())
                .temperature(request.getTemperature())
                .density(request.getDensity())
                .observations(request.getObservations())
                .build();
        
        if (request.getHarvestBatchId() != null) {
            HarvestBatch batch = harvestBatchRepository.findById(request.getHarvestBatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Harvest batch not found: " + request.getHarvestBatchId()));
            entity.setHarvestBatch(batch);
        }
        
        return toResponse(repository.save(entity));
    }
    
    @Transactional
    public HumusProductionResponse update(UUID id, HumusProductionRequest request) {
        HumusProduction entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Humus production not found: " + id));
        
        entity.setProductionDate(request.getProductionDate());
        entity.setQuantityLiters(request.getQuantityLiters());
        entity.setEc(request.getEc());
        entity.setPh(request.getPh());
        entity.setPpm(request.getPpm());
        entity.setTemperature(request.getTemperature());
        entity.setDensity(request.getDensity());
        entity.setObservations(request.getObservations());
        
        if (request.getHarvestBatchId() != null) {
            HarvestBatch batch = harvestBatchRepository.findById(request.getHarvestBatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Harvest batch not found: " + request.getHarvestBatchId()));
            entity.setHarvestBatch(batch);
        } else {
            entity.setHarvestBatch(null);
        }
        
        return toResponse(repository.save(entity));
    }
    
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Humus production not found: " + id);
        }
        repository.deleteById(id);
    }
    
    private HumusProductionResponse toResponse(HumusProduction entity) {
        return HumusProductionResponse.builder()
                .id(entity.getId())
                .productionDate(entity.getProductionDate())
                .harvestBatchId(entity.getHarvestBatch() != null ? entity.getHarvestBatch().getId() : null)
                .harvestBatchInfo(entity.getHarvestBatch() != null ? 
                        entity.getHarvestBatch().getHarvestDate().toString() : null)
                .quantityLiters(entity.getQuantityLiters())
                .ec(entity.getEc())
                .ph(entity.getPh())
                .ppm(entity.getPpm())
                .temperature(entity.getTemperature())
                .density(entity.getDensity())
                .observations(entity.getObservations())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
