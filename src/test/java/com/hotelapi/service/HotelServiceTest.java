package com.hotelapi.service;

import com.hotelapi.dto.*;
import com.hotelapi.exception.ResourceNotFoundException;
import com.hotelapi.exception.ValidationException;
import com.hotelapi.mapper.HotelMapper;
import com.hotelapi.model.Address;
import com.hotelapi.model.Amenity;
import com.hotelapi.model.ArrivalTime;
import com.hotelapi.model.Contacts;
import com.hotelapi.model.Hotel;
import com.hotelapi.repository.AmenityRepository;
import com.hotelapi.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private HotelMapper hotelMapper;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private Hotel hotel;
    private HotelSummaryDTO hotelSummaryDTO;
    private HotelDetailDTO hotelDetailDTO;
    private HotelCreateDTO hotelCreateDTO;
    private Address address;
    private Contacts contacts;
    private ArrivalTime arrivalTime;
    private Set<Amenity> amenities;

    @BeforeEach
    void setUp() {
        address = Address.builder()
                .houseNumber(9)
                .street("Pobediteley Avenue")
                .city("Minsk")
                .country("Belarus")
                .postCode("220004")
                .build();

        contacts = Contacts.builder()
                .phone("+375 17 309-80-00")
                .email("doubletreeminsk.info@hilton.com")
                .build();

        arrivalTime = ArrivalTime.builder()
                .checkIn("14:00")
                .checkOut("15:00")
                .build();

        amenities = new HashSet<>();
        amenities.add(Amenity.builder().id(1L).name("Free parking").build());
        amenities.add(Amenity.builder().id(2L).name("Free WiFi").build());

        hotel = Hotel.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .description("Test description")
                .brand("Hilton")
                .address(address)
                .contacts(contacts)
                .arrivalTime(arrivalTime)
                .amenities(amenities)
                .build();

        hotelSummaryDTO = HotelSummaryDTO.builder()
                .id(1L)
                .name("DoubleTree by Hilton Minsk")
                .description("Test description")
                .address("9 Pobediteley Avenue, Minsk, 220004, Belarus")
                .phone("+375 17 309-80-00")
                .build();

        AddressDTO addressDTO = AddressDTO.builder()
                .houseNumber(9)
                .street("Pobediteley Avenue")
                .city("Minsk")
                .country("Belarus")
                .postCode("220004")
                .build();

        ContactsDTO contactsDTO = ContactsDTO.builder()
                .phone("+375 17 309-80-00")
                .email("doubletreeminsk.info@hilton.com")
                .build();

        ArrivalTimeDTO arrivalTimeDTO = ArrivalTimeDTO.builder()
                .checkIn("14:00")
                .checkOut("15:00")
                .build();

        hotelCreateDTO = HotelCreateDTO.builder()
                .name("DoubleTree by Hilton Minsk")
                .description("Test description")
                .brand("Hilton")
                .address(addressDTO)
                .contacts(contactsDTO)
                .arrivalTime(arrivalTimeDTO)
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
    void getAllHotels_ShouldReturnListOfHotels() {
        List<Hotel> hotels = Collections.singletonList(hotel);
        when(hotelRepository.findAll()).thenReturn(hotels);
        when(hotelMapper.toSummaryDTOList(hotels)).thenReturn(Collections.singletonList(hotelSummaryDTO));

        List<HotelSummaryDTO> result = hotelService.getAllHotels();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("DoubleTree by Hilton Minsk");
        verify(hotelRepository, times(1)).findAll();
        verify(hotelMapper, times(1)).toSummaryDTOList(hotels);
    }

    @Test
    void getHotelById_WithValidId_ShouldReturnHotel() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelMapper.toDetailDTO(hotel)).thenReturn(hotelDetailDTO);

        HotelDetailDTO result = hotelService.getHotelById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("DoubleTree by Hilton Minsk");
        assertThat(result.getAmenities()).hasSize(2);
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    void getHotelById_WithInvalidId_ShouldThrowException() {
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.getHotelById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Hotel not found with id: 999");
        verify(hotelRepository, times(1)).findById(999L);
    }

    @Test
    void searchHotels_WithValidParams_ShouldReturnFilteredList() {
        List<Hotel> hotels = Collections.singletonList(hotel);

        when(hotelRepository.searchHotels(
                eq("Hilton"),
                isNull(),
                eq("Minsk"),
                isNull(),
                isNull()
        )).thenReturn(hotels);

        when(hotelMapper.toSummaryDTOList(hotels)).thenReturn(Collections.singletonList(hotelSummaryDTO));

        List<HotelSummaryDTO> result = hotelService.searchHotels("Hilton", null, "Minsk", null, null);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        verify(hotelRepository, times(1)).searchHotels(
                eq("Hilton"),
                isNull(),
                eq("Minsk"),
                isNull(),
                isNull()
        );
    }

    @Test
    void createHotel_WithValidData_ShouldReturnCreatedHotel() {
        when(hotelMapper.toEntity(hotelCreateDTO)).thenReturn(hotel);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);
        when(hotelMapper.toSummaryDTO(hotel)).thenReturn(hotelSummaryDTO);

        HotelSummaryDTO result = hotelService.createHotel(hotelCreateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("DoubleTree by Hilton Minsk");
        verify(hotelRepository, times(1)).save(any(Hotel.class));
        verify(hotelMapper, times(1)).toEntity(hotelCreateDTO);
    }

    @Test
    void createHotel_WithInvalidCheckOut_ShouldThrowException() {
        ArrivalTimeDTO invalidArrivalTime = ArrivalTimeDTO.builder()
                .checkIn("14:00")
                .checkOut("13:00")  // Check-out before check-in
                .build();
        hotelCreateDTO.setArrivalTime(invalidArrivalTime);

        assertThatThrownBy(() -> hotelService.createHotel(hotelCreateDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Check-out time must be after check-in time");

        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    @Test
    void addAmenitiesToHotel_WithValidData_ShouldAddAmenities() {
        List<String> amenityNames = Arrays.asList("Free parking", "Free WiFi", "New Amenity");
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(amenityRepository.findByName("Free parking")).thenReturn(Optional.of(Amenity.builder().id(1L).name("Free parking").build()));
        when(amenityRepository.findByName("Free WiFi")).thenReturn(Optional.of(Amenity.builder().id(2L).name("Free WiFi").build()));
        when(amenityRepository.findByName("New Amenity")).thenReturn(Optional.empty());
        when(amenityRepository.save(any(Amenity.class))).thenReturn(Amenity.builder().id(3L).name("New Amenity").build());

        hotelService.addAmenitiesToHotel(1L, amenityNames);

        verify(hotelRepository, times(1)).findById(1L);
        verify(amenityRepository, times(3)).findByName(anyString());
        verify(amenityRepository, times(1)).save(any(Amenity.class));
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void addAmenitiesToHotel_WithInvalidHotelId_ShouldThrowException() {
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.addAmenitiesToHotel(999L, Collections.singletonList("Free parking")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Hotel not found with id: 999");
        verify(hotelRepository, times(1)).findById(999L);
        verify(amenityRepository, never()).findByName(anyString());
    }

    @Test
    void getHistogram_ByCity_ShouldReturnValidHistogram() {
        List<Object[]> cityCounts = new ArrayList<>();
        cityCounts.add(new Object[]{"Minsk", 5L});
        cityCounts.add(new Object[]{"Moscow", 3L});

        when(hotelRepository.countByCity()).thenReturn(cityCounts);

        HistogramResponse result = hotelService.getHistogram("city");

        assertThat(result.getData()).isNotEmpty();
        assertThat(result.getData()).containsEntry("Minsk", 5L);
        assertThat(result.getData()).containsEntry("Moscow", 3L);
        verify(hotelRepository, times(1)).countByCity();
    }

    @Test
    void getHistogram_ByAmenities_ShouldReturnValidHistogram() {
        List<Object[]> amenityCounts = new ArrayList<>();
        amenityCounts.add(new Object[]{"Free parking", 10L});
        amenityCounts.add(new Object[]{"Free WiFi", 8L});

        when(hotelRepository.countByAmenities()).thenReturn(amenityCounts);

        HistogramResponse result = hotelService.getHistogram("amenities");

        assertThat(result.getData()).isNotEmpty();
        assertThat(result.getData()).containsEntry("Free parking", 10L);
        assertThat(result.getData()).containsEntry("Free WiFi", 8L);
        verify(hotelRepository, times(1)).countByAmenities();
    }

    @Test
    void getHistogram_WithInvalidParam_ShouldThrowException() {
        assertThatThrownBy(() -> hotelService.getHistogram("invalid"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid histogram parameter: invalid");
    }
}
