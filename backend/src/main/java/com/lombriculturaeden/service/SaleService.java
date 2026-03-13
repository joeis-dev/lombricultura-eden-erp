package com.lombriculturaeden.service;

import com.lombriculturaeden.dto.ProductResponse;
import com.lombriculturaeden.dto.SaleRequest;
import com.lombriculturaeden.dto.SaleResponse;
import com.lombriculturaeden.entity.Customer;
import com.lombriculturaeden.entity.Product;
import com.lombriculturaeden.entity.Sale;
import com.lombriculturaeden.entity.SaleDetail;
import com.lombriculturaeden.exception.ResourceNotFoundException;
import com.lombriculturaeden.repository.CustomerRepository;
import com.lombriculturaeden.repository.ProductRepository;
import com.lombriculturaeden.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleService {
    
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    
    @Transactional(readOnly = true)
    public List<SaleResponse> findAll() {
        return saleRepository.findAllByOrderBySaleDateDesc().stream()
                .map(SaleResponse::fromEntity)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public SaleResponse findById(UUID id) {
        return saleRepository.findById(id)
                .map(SaleResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found: " + id));
    }
    
    @Transactional
    public SaleResponse create(SaleRequest request) {
        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.getCustomerId()));
        }
        
        BigDecimal total = BigDecimal.ZERO;
        
        Sale sale = Sale.builder()
                .customer(customer)
                .saleDate(request.getSaleDate())
                .status(request.getStatus() != null ? request.getStatus() : Sale.SaleStatus.PENDING)
                .observations(request.getObservations())
                .build();
        
        for (SaleRequest.SaleDetailRequest detailRequest : request.getDetails()) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + detailRequest.getProductId()));
            
            BigDecimal subtotal = detailRequest.getQuantity().multiply(detailRequest.getUnitPrice());
            total = total.add(subtotal);
            
            SaleDetail detail = SaleDetail.builder()
                    .sale(sale)
                    .product(product)
                    .quantity(detailRequest.getQuantity())
                    .unitPrice(detailRequest.getUnitPrice())
                    .subtotal(subtotal)
                    .build();
            
            sale.getDetails().add(detail);
        }
        
        sale.setTotal(total);
        
        return SaleResponse.fromEntity(saleRepository.save(sale));
    }
    
    @Transactional
    public SaleResponse updateStatus(UUID id, Sale.SaleStatus status) {
        Sale entity = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found: " + id));
        
        entity.setStatus(status);
        return SaleResponse.fromEntity(saleRepository.save(entity));
    }
}
