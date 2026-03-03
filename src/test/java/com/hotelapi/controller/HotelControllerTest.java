package com.hotelapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelapi.dto.*;
import com.hotelapi.exception.ResourceNotFoundException;
import com.hotelapi.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    @Autowired
    private ObjectMapper objectMapper;

    private HotelSummaryDTO hotelSummaryDTO;
    private HotelDetailDTO hotelDetailDTO;
    private HotelCreateDTO hotelCreateDTO;
    private AddressDTO addressDTO;
    private ContactsDTO contactsDTO;
    private ArrivalTimeDTO arrivalTimeDTO;

    @BeforeEach
    void setUp() {
        addressDTO = AddressDTO.builder()
                .houseNumber(9)
                .street("Pobediteley Avenue")
                .city("Minsk")
                .country("Belarus")
                .postCode("220004")
                .build();

        contactsDTO = ContactsDTO.builder()
                .phone("+375 17 309-80-00")
                .email("doubletreeminsk.info@hilton.com")
                .build();

        arrivalTimeDTO = ArrivalTimeDTO.builder()
                .checkIn("14:00")
                .checkOut("12:00")
                .build();

        hotelCreateDTO = HotelCreateDTO.builder()
                .name("DoubleTree by Hilton Minsk")
                .description("Test description")
                .brand("Hilton")
                .address(addressDTO)
                .contacts(contactsDTO)
                .arrivalTime(arrivalTimeDTO)
                .build();

        hotelSummaryDTO = HotelSummaryDTO.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .description("Test description")
                .address("9 Pobediteley Avenue, Minsk, 220004, Belarus")
                .phone("+375 17 309-80-00")
                .build();

        hotelDetailDTO = HotelDetailDTO.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .description("Test description")
                .brand("Hilton")
                .address(addressDTO)
                .contacts(contactsDTO)
                .arrivalTime(arrivalTimeDTO)
                .amenities(Arrays.asList("Free parking", "Free WiFi"))
                .build();
    }

    @Test
    void getAllHotels_ShouldReturnListOfHotels() throws Exception {
        when(hotelService.getAllHotels()).thenReturn(Collections.singletonList(hotelSummaryDTO));

        mockMvc.perform(get("/api/v1/hotels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("DoubleTree by Hilton Minsk"));
    }

    @Test
    void getHotelById_WithValidId_ShouldReturnHotel() throws Exception {
        when(hotelService.getHotelById(1L)).thenReturn(hotelDetailDTO);

        mockMvc.perform(get("/api/v1/hotels/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("DoubleTree by Hilton Minsk"));
    }

    @Test
    void getHotelById_WithInvalidId_ShouldReturn404() throws Exception {
        when(hotelService.getHotelById(999L))
                .thenThrow(new ResourceNotFoundException("Hotel not found with id: 999"));

        mockMvc.perform(get("/api/v1/hotels/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchHotels_WithParams_ShouldReturnFilteredList() throws Exception {
        List<HotelSummaryDTO> hotelList = Collections.singletonList(hotelSummaryDTO);

        when(hotelService.searchHotels(
                isNull(),      // name
                eq("Hilton"),  // brand
                eq("Minsk"),   // city
                isNull(),      // country
                isNull()       // amenity
        )).thenReturn(hotelList);

        mockMvc.perform(get("/api/v1/search")
                        .param("city", "Minsk")
                        .param("brand", "Hilton")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("DoubleTree by Hilton Minsk"));

        verify(hotelService, times(1)).searchHotels(
                isNull(),
                eq("Hilton"),
                eq("Minsk"),
                isNull(),
                isNull()
        );
    }

    @Test
    void createHotel_WithValidData_ShouldReturnCreatedHotel() throws Exception {
        when(hotelService.createHotel(any(HotelCreateDTO.class))).thenReturn(hotelSummaryDTO);

        mockMvc.perform(post("/api/v1/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotelCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("DoubleTree by Hilton Minsk"));

        verify(hotelService, times(1)).createHotel(any(HotelCreateDTO.class));
    }

    @Test
    void createHotel_WithInvalidData_ShouldReturn400() throws Exception {
        hotelCreateDTO.setName(null);

        mockMvc.perform(post("/api/v1/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotelCreateDTO)))
                .andExpect(status().isBadRequest());

        verify(hotelService, never()).createHotel(any(HotelCreateDTO.class));
    }

    @Test
    void addAmenitiesToHotel_WithValidData_ShouldReturn200() throws Exception {
        doNothing().when(hotelService).addAmenitiesToHotel(eq(1L), anyList());

        mockMvc.perform(post("/api/v1/hotels/{id}/amenities", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList("Free parking", "Free WiFi"))))
                .andExpect(status().isOk());

        verify(hotelService, times(1)).addAmenitiesToHotel(eq(1L), anyList());
    }

    @Test
    void addAmenitiesToHotel_WithInvalidHotelId_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Hotel not found with id: 999"))
                .when(hotelService).addAmenitiesToHotel(eq(999L), anyList());

        mockMvc.perform(post("/api/v1/hotels/{id}/amenities", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList("Free parking"))))
                .andExpect(status().isNotFound());

        verify(hotelService, times(1)).addAmenitiesToHotel(eq(999L), anyList());
    }

    @Test
    void getHistogram_WithValidParam_ShouldReturnHistogram() throws Exception {
        Map<String, Long> histogramData = new HashMap<>();
        histogramData.put("Minsk", 5L);
        histogramData.put("Moscow", 3L);

        HistogramResponse response = HistogramResponse.builder().data(histogramData).build();
        when(hotelService.getHistogram("city")).thenReturn(response);

        mockMvc.perform(get("/api/v1/histogram/{param}", "city")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.Minsk").value(5))
                .andExpect(jsonPath("$.data.Moscow").value(3));
    }

    @Test
    void getHistogram_WithInvalidParam_ShouldReturnEmptyHistogram() throws Exception {
        HistogramResponse emptyResponse = HistogramResponse.builder()
                .data(Collections.emptyMap())
                .build();

        when(hotelService.getHistogram("invalid")).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/v1/histogram/{param}", "invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Ожидаем 200
                .andExpect(jsonPath("$.data").isEmpty());  // Проверяем, что данные пустые

        verify(hotelService, times(1)).getHistogram("invalid");
    }
}
