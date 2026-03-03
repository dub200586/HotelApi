package com.hotelapi.service;

import com.hotelapi.dto.*;

import java.util.List;

public interface HotelService {
    List<HotelSummaryDTO> getAllHotels();
    HotelDetailDTO getHotelById(Long id);
    List<HotelSummaryDTO> searchHotels(String name, String brand, String city, String country, String amenity);
    HotelSummaryDTO createHotel(HotelCreateDTO hotelCreateDTO);
    void addAmenitiesToHotel(Long id, List<String> amenities);
    HistogramResponse getHistogram(String param);
}
