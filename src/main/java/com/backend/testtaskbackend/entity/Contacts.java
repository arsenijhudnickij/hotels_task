package com.backend.testtaskbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contacts {
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "email")
    private String email;
}