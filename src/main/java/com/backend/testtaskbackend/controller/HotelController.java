package com.backend.testtaskbackend.controller;

import com.backend.testtaskbackend.dto.HotelShortDto;
import com.backend.testtaskbackend.entity.Hotel;
import com.backend.testtaskbackend.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Hotels", description = "Методы для работы с отелями")
public class HotelController {

    private final HotelService hotelService;

    @Operation(summary = "Получение списка всех отелей (кратко)")
    @GetMapping("/hotels")
    public List<HotelShortDto> getAllHotels() {
        return hotelService.getAllHotelsShort();
    }

    @Operation(summary = "Получение полной информации об отеле по ID")
    @GetMapping("/hotels/{id}")
    public Hotel getHotelById(@PathVariable Long id) {
        return hotelService.getHotelDetails(id);
    }

    @Operation(summary = "Поиск отелей по параметрам")
    @GetMapping("/search")
    public List<HotelShortDto> searchHotels(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<String> amenities) {
        return hotelService.search(name, brand, city, country, amenities);
    }

    @Operation(summary = "Создание нового отеля")
    @PostMapping("/hotels")
    public Hotel createHotel(@RequestBody Hotel hotel) {
        return hotelService.createHotel(hotel);
    }

    @Operation(summary = "Добавление удобств (amenities) к отелю")
    @PostMapping("/hotels/{id}/amenities")
    public void addAmenities(@PathVariable Long id, @RequestBody List<String> amenities) {
        hotelService.addAmenities(id, amenities);
    }

    @Operation(summary = "Получение гистограммы по параметру")
    @GetMapping("/histogram/{param}")
    public Map<String, Long> getHistogram(@PathVariable String param) {
        return hotelService.getHistogram(param);
    }
}