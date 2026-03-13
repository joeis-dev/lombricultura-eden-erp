package com.lombriculturaeden.service;

import com.lombriculturaeden.dto.CustomerRequest;
import com.lombriculturaeden.dto.CustomerResponse;
import com.lombriculturaeden.entity.Customer;
import com.lombriculturaeden.exception.ResourceNotFoundException;
import com.lombriculturaeden.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository repository;
    
    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return repository.findAllByOrderByNameAsc().stream()
                .map(CustomerResponse::fromEntity)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {
        return repository.findById(id)
                .map(CustomerResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
    }
    
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (request.getDocument() != null && repository.existsByDocument(request.getDocument())) {
            throw new IllegalArgumentException("Customer already exists with document: " + request.getDocument());
        }
        
        Customer entity = Customer.builder()
                .name(request.getName())
                .document(request.getDocument())
                .documentType(request.getDocumentType())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();
        
        return CustomerResponse.fromEntity(repository.save(entity));
    }
    
    @Transactional
    public CustomerResponse update(UUID id, CustomerRequest request) {
        Customer entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        
        if (request.getDocument() != null && !request.getDocument().equals(entity.getDocument()) 
                && repository.existsByDocument(request.getDocument())) {
            throw new IllegalArgumentException("Customer already exists with document: " + request.getDocument());
        }
        
        entity.setName(request.getName());
        entity.setDocument(request.getDocument());
        entity.setDocumentType(request.getDocumentType());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        
        return CustomerResponse.fromEntity(repository.save(entity));
    }
    
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found: " + id);
        }
        repository.deleteById(id);
    }
}
