package com.backend.testtaskbackend;

import com.backend.testtaskbackend.entity.Hotel;
import com.backend.testtaskbackend.entity.Address;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateAndGetHotel() throws Exception {
        // 1. Создаем объект
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setBrand("Test Brand");
        hotel.setDescription("Test Description");

        Address address = new Address();
        address.setCity("Minsk");
        address.setStreet("Test St");
        address.setHouseNumber(1);
        address.setCountry("Belarus");
        address.setPostCode("12345");

        hotel.setAddress(address);

        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Hotel"));

        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Hotel"));
    }
}