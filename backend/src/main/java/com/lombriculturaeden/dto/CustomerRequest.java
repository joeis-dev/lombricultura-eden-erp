package com.lombriculturaeden.dto;

import com.lombriculturaeden.entity.Customer;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String document;
    
    private Customer.DocumentType documentType;
    
    private String email;
    
    private String phone;
    
    private String address;
}
