package com.backend.testtaskbackend.service;

import com.backend.testtaskbackend.dto.*;
import com.backend.testtaskbackend.entity.Hotel;
import com.backend.testtaskbackend.repository.HotelRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

    public HotelFullDto getHotelDetails(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
        return convertToFullDto(hotel);
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

                for (String amenity : amenities) {
                    Subquery<Long> subquery = query.subquery(Long.class);
                    jakarta.persistence.criteria.Root<Hotel> subRoot = subquery.from(Hotel.class);
                    Join<Hotel, String> amenitiesJoin = subRoot.join("amenities");
                    subquery.select(subRoot.get("id"))
                            .where(
                                    cb.equal(subRoot.get("id"), root.get("id")),
                                    cb.like(cb.lower(amenitiesJoin), "%" + amenity.toLowerCase() + "%")
                            );
                    predicates.add(cb.exists(subquery));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return hotelRepository.findAll(spec).stream()
                .map(this::convertToShortDto)
                .collect(Collectors.toList());
    }

    public HotelShortDto createHotel(Hotel hotel) {
        hotel.setAmenities(null);
        Hotel savedHotel = hotelRepository.save(hotel);
        return convertToShortDto(savedHotel);
    }

    // 5. Добавление удобств
    public void addAmenities(Long id, List<String> amenities) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
        if (hotel.getAmenities() == null) {
            hotel.setAmenities(new ArrayList<>());
        }

        LinkedHashSet<String> uniqueAmenities = new LinkedHashSet<>(hotel.getAmenities());
        uniqueAmenities.addAll(amenities);
        hotel.setAmenities(new ArrayList<>(uniqueAmenities));
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
        String fullAddress = null;
        if (hotel.getAddress() != null && hotel.getAddress().getHouseNumber() != null 
                && hotel.getAddress().getStreet() != null && hotel.getAddress().getCity() != null) {
            fullAddress = hotel.getAddress().getHouseNumber() + " " +
                    hotel.getAddress().getStreet() + ", " +
                    hotel.getAddress().getCity() + ", " +
                    (hotel.getAddress().getPostCode() != null ? hotel.getAddress().getPostCode() + ", " : "") +
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

    private HotelFullDto convertToFullDto(Hotel hotel) {
        AddressDto addressDto = null;
        if (hotel.getAddress() != null) {
            addressDto = new AddressDto(
                    hotel.getAddress().getHouseNumber(),
                    hotel.getAddress().getStreet(),
                    hotel.getAddress().getCity(),
                    hotel.getAddress().getCountry(),
                    hotel.getAddress().getPostCode()
            );
        }

        ContactsDto contactsDto = null;
        if (hotel.getContacts() != null) {
            contactsDto = new ContactsDto(
                    hotel.getContacts().getPhone(),
                    hotel.getContacts().getEmail()
            );
        }

        ArrivalTimeDto arrivalTimeDto = null;
        if (hotel.getArrivalTime() != null) {
            arrivalTimeDto = new ArrivalTimeDto(
                    hotel.getArrivalTime().getCheckIn(),
                    hotel.getArrivalTime().getCheckOut()
            );
        }

        return new HotelFullDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                hotel.getBrand(),
                addressDto,
                contactsDto,
                arrivalTimeDto,
                hotel.getAmenities()
        );
    }
}