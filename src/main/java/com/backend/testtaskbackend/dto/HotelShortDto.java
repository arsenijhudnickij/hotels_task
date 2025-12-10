package com.backend.testtaskbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelShortDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String phone;
}