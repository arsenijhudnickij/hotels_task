package com.backend.testtaskbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrivalTimeDto {
    private String checkIn;
    private String checkOut;
}

