package com.lombriculturaeden.integration;

import com.lombriculturaeden.dto.HarvestBatchRequest;
import com.lombriculturaeden.entity.HarvestBatch;
import com.lombriculturaeden.repository.HarvestBatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class HarvestBatchControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private HarvestBatchRepository repository;
    
    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }
    
    @Test
    void shouldCreateHarvestBatch() throws Exception {
        String requestBody = """
            {
                "harvestDate": "2024-01-15",
                "quantityKg": 100.00,
                "averageWeight": 5.5,
                "observations": "Test batch"
            }
            """;
        
        mockMvc.perform(post("/api/v1/harvest-batches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.quantityKg").value(100.00));
    }
    
    @Test
    void shouldGetAllHarvestBatches() throws Exception {
        HarvestBatch batch = HarvestBatch.builder()
                .harvestDate(LocalDate.now())
                .quantityKg(new BigDecimal("100.00"))
                .build();
        repository.save(batch);
        
        mockMvc.perform(get("/api/v1/harvest-batches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    
    @Test
    void shouldGetHarvestBatchById() throws Exception {
        HarvestBatch batch = HarvestBatch.builder()
                .harvestDate(LocalDate.now())
                .quantityKg(new BigDecimal("100.00"))
                .build();
        batch = repository.save(batch);
        
        mockMvc.perform(get("/api/v1/harvest-batches/" + batch.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(batch.getId().toString()));
    }
    
    @Test
    void shouldUpdateHarvestBatch() throws Exception {
        HarvestBatch batch = HarvestBatch.builder()
                .harvestDate(LocalDate.now())
                .quantityKg(new BigDecimal("100.00"))
                .build();
        batch = repository.save(batch);
        
        String requestBody = """
            {
                "harvestDate": "2024-01-20",
                "quantityKg": 150.00
            }
            """;
        
        mockMvc.perform(put("/api/v1/harvest-batches/" + batch.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantityKg").value(150.00));
    }
    
    @Test
    void shouldDeleteHarvestBatch() throws Exception {
        HarvestBatch batch = HarvestBatch.builder()
                .harvestDate(LocalDate.now())
                .quantityKg(new BigDecimal("100.00"))
                .build();
        batch = repository.save(batch);
        
        mockMvc.perform(delete("/api/v1/harvest-batches/" + batch.getId()))
                .andExpect(status().isNoContent());
        
        mockMvc.perform(get("/api/v1/harvest-batches/" + batch.getId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldReturn400_WhenInvalidRequest() throws Exception {
        String requestBody = """
            {
                "quantityKg": -10.00
            }
            """;
        
        mockMvc.perform(post("/api/v1/harvest-batches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
