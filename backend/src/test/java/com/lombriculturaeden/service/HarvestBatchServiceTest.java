package com.lombriculturaeden.service;

import com.lombriculturaeden.dto.HarvestBatchRequest;
import com.lombriculturaeden.dto.HarvestBatchResponse;
import com.lombriculturaeden.entity.HarvestBatch;
import com.lombriculturaeden.exception.ResourceNotFoundException;
import com.lombriculturaeden.repository.HarvestBatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HarvestBatchServiceTest {
    
    @Mock
    private HarvestBatchRepository repository;
    
    @InjectMocks
    private HarvestBatchService service;
    
    private HarvestBatch harvestBatch;
    private UUID id;
    
    @BeforeEach
    void setUp() throws Exception {
        id = UUID.randomUUID();
        harvestBatch = HarvestBatch.builder()
                .harvestDate(LocalDate.now())
                .quantityKg(new BigDecimal("100.00"))
                .averageWeight(new BigDecimal("5.5"))
                .observations("Test batch")
                .build();
        Field idField = HarvestBatch.class.getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(harvestBatch, id);
    }
    
    @Test
    void findAll_ShouldReturnAllBatches() {
        when(repository.findAllByOrderByHarvestDateDesc()).thenReturn(List.of(harvestBatch));
        
        List<HarvestBatchResponse> result = service.findAll();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(id, result.get(0).getId());
    }
    
    @Test
    void findById_ShouldReturnBatch_WhenExists() {
        when(repository.findById(id)).thenReturn(Optional.of(harvestBatch));
        
        HarvestBatchResponse result = service.findById(id);
        
        assertNotNull(result);
        assertEquals(id, result.getId());
    }
    
    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        when(repository.findById(id)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }
    
    @Test
    void create_ShouldSaveNewBatch() {
        HarvestBatchRequest request = HarvestBatchRequest.builder()
                .harvestDate(LocalDate.now())
                .quantityKg(new BigDecimal("100.00"))
                .build();
        
        when(repository.save(any(HarvestBatch.class))).thenReturn(harvestBatch);
        
        HarvestBatchResponse result = service.create(request);
        
        assertNotNull(result);
        verify(repository).save(any(HarvestBatch.class));
    }
    
    @Test
    void update_ShouldUpdateExistingBatch() {
        HarvestBatchRequest request = HarvestBatchRequest.builder()
                .harvestDate(LocalDate.now())
                .quantityKg(new BigDecimal("150.00"))
                .build();
        
        when(repository.findById(id)).thenReturn(Optional.of(harvestBatch));
        when(repository.save(any(HarvestBatch.class))).thenReturn(harvestBatch);
        
        HarvestBatchResponse result = service.update(id, request);
        
        assertNotNull(result);
        verify(repository).save(any(HarvestBatch.class));
    }
    
    @Test
    void delete_ShouldDeleteBatch_WhenExists() {
        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);
        
        service.delete(id);
        
        verify(repository).deleteById(id);
    }
    
    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        when(repository.existsById(id)).thenReturn(false);
        
        assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
    }
}
