package com.backend.testtaskbackend.dto;

public class ArrivalTimeDto {
    private String checkIn;
    private String checkOut;

    public ArrivalTimeDto() {
    }

    public ArrivalTimeDto(String checkIn, String checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }
}

