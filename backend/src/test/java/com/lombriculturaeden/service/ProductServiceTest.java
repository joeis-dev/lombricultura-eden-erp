package com.lombriculturaeden.service;

import com.lombriculturaeden.dto.ProductRequest;
import com.lombriculturaeden.dto.ProductResponse;
import com.lombriculturaeden.entity.Product;
import com.lombriculturaeden.exception.ResourceNotFoundException;
import com.lombriculturaeden.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository repository;
    
    @InjectMocks
    private ProductService service;
    
    private Product product;
    private UUID id;
    
    @BeforeEach
    void setUp() throws Exception {
        id = UUID.randomUUID();
        product = Product.builder()
                .name("Humus Liquido")
                .type(Product.ProductType.HUMUS_LIQUID)
                .description("Humus liquido de lombriz")
                .unitOfMeasure("L")
                .currentStock(new BigDecimal("100.00"))
                .minimumStock(new BigDecimal("20.00"))
                .unitPrice(new BigDecimal("5.00"))
                .unitCost(new BigDecimal("2.00"))
                .build();
        Field idField = Product.class.getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(product, id);
    }
    
    @Test
    void findAll_ShouldReturnAllProducts() {
        when(repository.findAllByOrderByNameAsc()).thenReturn(List.of(product));
        
        List<ProductResponse> result = service.findAll();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Humus Liquido", result.get(0).getName());
    }
    
    @Test
    void findById_ShouldReturnProduct_WhenExists() {
        when(repository.findById(id)).thenReturn(Optional.of(product));
        
        ProductResponse result = service.findById(id);
        
        assertNotNull(result);
        assertEquals(id, result.getId());
    }
    
    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        when(repository.findById(id)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }
    
    @Test
    void findLowStock_ShouldReturnProductsBelowMinimum() throws Exception {
        UUID lowStockId = UUID.randomUUID();
        Product lowStockProduct = Product.builder()
                .name("Low Stock Product")
                .type(Product.ProductType.HUMUS_LIQUID)
                .unitOfMeasure("L")
                .currentStock(new BigDecimal("10.00"))
                .minimumStock(new BigDecimal("20.00"))
                .build();
        Field idField = Product.class.getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(lowStockProduct, lowStockId);
        
        when(repository.findLowStock()).thenReturn(List.of(lowStockProduct));
        
        List<ProductResponse> result = service.findLowStock();
        
        assertNotNull(result);
        assertTrue(result.get(0).isLowStock());
    }
    
    @Test
    void create_ShouldSaveNewProduct() {
        ProductRequest request = ProductRequest.builder()
                .name("New Product")
                .type(Product.ProductType.HUMUS_LIQUID)
                .unitOfMeasure("L")
                .build();
        
        when(repository.existsByName("New Product")).thenReturn(false);
        when(repository.save(any(Product.class))).thenReturn(product);
        
        ProductResponse result = service.create(request);
        
        assertNotNull(result);
        verify(repository).save(any(Product.class));
    }
    
    @Test
    void create_ShouldThrowException_WhenNameExists() {
        ProductRequest request = ProductRequest.builder()
                .name("Existing Product")
                .type(Product.ProductType.HUMUS_LIQUID)
                .unitOfMeasure("L")
                .build();
        
        when(repository.existsByName("Existing Product")).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> service.create(request));
    }
    
    @Test
    void update_ShouldUpdateExistingProduct() {
        ProductRequest request = ProductRequest.builder()
                .name("Updated Product")
                .type(Product.ProductType.HUMUS_SOLID)
                .unitOfMeasure("KG")
                .build();
        
        when(repository.findById(id)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenReturn(product);
        
        ProductResponse result = service.update(id, request);
        
        assertNotNull(result);
        verify(repository).save(any(Product.class));
    }
    
    @Test
    void delete_ShouldDeleteProduct_WhenExists() {
        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);
        
        service.delete(id);
        
        verify(repository).deleteById(id);
    }
}
