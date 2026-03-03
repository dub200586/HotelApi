package com.hotelapi.service;

import com.hotelapi.dto.*;
import com.hotelapi.exception.ResourceNotFoundException;
import com.hotelapi.exception.ValidationException;
import com.hotelapi.mapper.HotelMapper;
import com.hotelapi.model.Amenity;
import com.hotelapi.model.Hotel;
import com.hotelapi.repository.AmenityRepository;
import com.hotelapi.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final HotelMapper hotelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<HotelSummaryDTO> getAllHotels() {
        log.info("Fetching all hotels");
        List<Hotel> hotels = hotelRepository.findAll();
        return hotelMapper.toSummaryDTOList(hotels);
    }

    @Override
    @Transactional(readOnly = true)
    public HotelDetailDTO getHotelById(Long id) {
        log.info("Fetching hotel with id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        return hotelMapper.toDetailDTO(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelSummaryDTO> searchHotels(String name, String brand, String city, String country, String amenity) {
        log.info("Searching hotels with filters - name: {}, brand: {}, city: {}, country: {}, amenity: {}",
                name, brand, city, country, amenity);

        List<Hotel> hotels = hotelRepository.searchHotels(name, brand, city, country, amenity);
        return hotelMapper.toSummaryDTOList(hotels);
    }

    @Override
    public HotelSummaryDTO createHotel(HotelCreateDTO hotelCreateDTO) {
        log.info("Creating new hotel: {}", hotelCreateDTO.getName());

        validateArrivalTime(hotelCreateDTO.getArrivalTime());

        Hotel hotel = hotelMapper.toEntity(hotelCreateDTO);
        hotel.setAmenities(new HashSet<>());

        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel created successfully with id: {}", savedHotel.getId());

        return hotelMapper.toSummaryDTO(savedHotel);
    }

    @Override
    public void addAmenitiesToHotel(Long id, List<String> amenityNames) {
        log.info("Adding amenities to hotel id: {}", id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        Set<Amenity> amenities = new HashSet<>();
        for (String amenityName : amenityNames) {
            Amenity amenity = amenityRepository.findByName(amenityName)
                    .orElseGet(() -> {
                        Amenity newAmenity = Amenity.builder()
                                .name(amenityName)
                                .build();
                        return amenityRepository.save(newAmenity);
                    });
            amenities.add(amenity);
        }

        hotel.getAmenities().addAll(amenities);
        hotelRepository.save(hotel);
        log.info("Amenities added successfully to hotel id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public HistogramResponse getHistogram(String param) {
        log.info("Generating histogram for param: {}", param);

        Map<String, Long> histogramData = new LinkedHashMap<>();

        switch (param.toLowerCase()) {
            case "city":
                hotelRepository.countByCity()
                        .forEach(obj -> histogramData.put((String) obj[0], (Long) obj[1]));
                break;
            case "country":
                hotelRepository.countByCountry()
                        .forEach(obj -> histogramData.put((String) obj[0], (Long) obj[1]));
                break;
            case "brand":
                hotelRepository.countByBrand()
                        .forEach(obj -> histogramData.put((String) obj[0], (Long) obj[1]));
                break;
            case "amenities":
                hotelRepository.countByAmenities()
                        .forEach(obj -> histogramData.put((String) obj[0], (Long) obj[1]));
                break;
            default:
                throw new ValidationException("Invalid histogram parameter: " + param +
                        ". Supported values: city, country, brand, amenities");
        }

        return HistogramResponse.builder().data(histogramData).build();
    }

    private void validateArrivalTime(ArrivalTimeDTO arrivalTime) {
        if (arrivalTime == null) {
            throw new ValidationException("Arrival time is required");
        }
        if (arrivalTime.getCheckOut() != null) {
            if (arrivalTime.getCheckIn().compareTo(arrivalTime.getCheckOut()) >= 0) {
                throw new ValidationException("Check-out time must be after check-in time");
            }
        }
    }
}
