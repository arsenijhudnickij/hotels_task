package com.backend.testtaskbackend.repository;

import com.backend.testtaskbackend.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    // Запрос для группировки по Бренду
    @Query("SELECT h.brand, COUNT(h) FROM Hotel h GROUP BY h.brand")
    List<Object[]> countByBrand();

    // Запрос для группировки по Городу
    @Query("SELECT h.address.city, COUNT(h) FROM Hotel h GROUP BY h.address.city")
    List<Object[]> countByCity();

    // Запрос для группировки по Стране
    @Query("SELECT h.address.country, COUNT(h) FROM Hotel h GROUP BY h.address.country")
    List<Object[]> countByCountry();

    // Запрос для группировки по Удобствам (требует JOIN, так как это список)
    @Query("SELECT a, COUNT(h) FROM Hotel h JOIN h.amenities a GROUP BY a")
    List<Object[]> countByAmenities();
}