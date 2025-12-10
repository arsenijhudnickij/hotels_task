package com.backend.testtaskbackend.service;

import com.backend.testtaskbackend.dto.HotelShortDto;
import com.backend.testtaskbackend.entity.Hotel;
import com.backend.testtaskbackend.repository.HotelRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<HotelShortDto> getAllHotelsShort() {
        return hotelRepository.findAll().stream()
                .map(this::convertToShortDto)
                .collect(Collectors.toList());
    }

    public Hotel getHotelDetails(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
    }

    public List<HotelShortDto> search(String name, String brand, String city, String country, List<String> amenities) {
        Specification<Hotel> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (brand != null && !brand.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), brand.toLowerCase()));
            }

            if (city != null && !city.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("address").get("city")), city.toLowerCase()));
            }

            if (country != null && !country.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("address").get("country")), country.toLowerCase()));
            }

            if (amenities != null && !amenities.isEmpty()) {
                Join<Hotel, String> amenitiesJoin = root.join("amenities");
                List<Predicate> amenitiesPredicates = new ArrayList<>();

                for (String amenity : amenities) {
                    amenitiesPredicates.add(
                            cb.like(cb.lower(amenitiesJoin), "%" + amenity.toLowerCase() + "%")
                    );
                }

                predicates.add(cb.or(amenitiesPredicates.toArray(new Predicate[0])));

                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return hotelRepository.findAll(spec).stream()
                .map(this::convertToShortDto)
                .collect(Collectors.toList());
    }

    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    // 5. Добавление удобств
    public void addAmenities(Long id, List<String> amenities) {
        Hotel hotel = getHotelDetails(id);
        if (hotel.getAmenities() == null) {
            hotel.setAmenities(new ArrayList<>());
        }
        hotel.getAmenities().addAll(amenities);
        hotelRepository.save(hotel);
    }

    // 6. Гистограммы
    public Map<String, Long> getHistogram(String param) {
        List<Object[]> results;

        switch (param.toLowerCase()) {
            case "brand" -> results = hotelRepository.countByBrand();
            case "city" -> results = hotelRepository.countByCity();
            case "country" -> results = hotelRepository.countByCountry();
            case "amenities" -> results = hotelRepository.countByAmenities();
            default -> throw new IllegalArgumentException("Unknown param: " + param);
        }

        Map<String, Long> histogram = new HashMap<>();
        for (Object[] result : results) {
            String key = (result[0] != null) ? (String) result[0] : "Unknown";
            Long count = (Long) result[1];
            histogram.put(key, count);
        }
        return histogram;
    }

    private HotelShortDto convertToShortDto(Hotel hotel) {
        String fullAddress = "";
        if (hotel.getAddress() != null) {
            fullAddress = hotel.getAddress().getHouseNumber() + " " +
                    hotel.getAddress().getStreet() + ", " +
                    hotel.getAddress().getCity() + ", " +
                    hotel.getAddress().getPostCode() + ", " +
                    hotel.getAddress().getCountry();
        }

        String phone = (hotel.getContacts() != null) ? hotel.getContacts().getPhone() : null;

        return new HotelShortDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                fullAddress,
                phone
        );
    }
}