package com.lombriculturaeden.service;

import com.lombriculturaeden.dto.HarvestBatchRequest;
import com.lombriculturaeden.dto.HarvestBatchResponse;
import com.lombriculturaeden.entity.HarvestBatch;
import com.lombriculturaeden.exception.ResourceNotFoundException;
import com.lombriculturaeden.repository.HarvestBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HarvestBatchService {
    
    private final HarvestBatchRepository repository;
    
    @Transactional(readOnly = true)
    public List<HarvestBatchResponse> findAll() {
        return repository.findAllByOrderByHarvestDateDesc().stream()
                .map(HarvestBatchResponse::fromEntity)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public HarvestBatchResponse findById(UUID id) {
        return repository.findById(id)
                .map(HarvestBatchResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest batch not found: " + id));
    }
    
    @Transactional
    public HarvestBatchResponse create(HarvestBatchRequest request) {
        HarvestBatch entity = HarvestBatch.builder()
                .harvestDate(request.getHarvestDate())
                .quantityKg(request.getQuantityKg())
                .averageWeight(request.getAverageWeight())
                .observations(request.getObservations())
                .build();
        
        return HarvestBatchResponse.fromEntity(repository.save(entity));
    }
    
    @Transactional
    public HarvestBatchResponse update(UUID id, HarvestBatchRequest request) {
        HarvestBatch entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest batch not found: " + id));
        
        entity.setHarvestDate(request.getHarvestDate());
        entity.setQuantityKg(request.getQuantityKg());
        entity.setAverageWeight(request.getAverageWeight());
        entity.setObservations(request.getObservations());
        
        return HarvestBatchResponse.fromEntity(repository.save(entity));
    }
    
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Harvest batch not found: " + id);
        }
        repository.deleteById(id);
    }
}
