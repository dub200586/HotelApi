package com.hotelapi.repository;

import com.hotelapi.model.Address;
import com.hotelapi.model.ArrivalTime;
import com.hotelapi.model.Contacts;
import com.hotelapi.model.Hotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class HotelRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel hotel1;
    private Hotel hotel2;

    @BeforeEach
    void setUp() {
        Address address1 = Address.builder()
                .houseNumber(9)
                .street("Pobediteley Avenue")
                .city("Minsk")
                .country("Belarus")
                .postCode("220004")
                .build();

        Contacts contacts1 = Contacts.builder()
                .phone("+375 17 309-80-00")
                .email("minsk@hilton.com")
                .build();

        ArrivalTime arrivalTime1 = ArrivalTime.builder()
                .checkIn("14:00")
                .checkOut("12:00")
                .build();

        hotel1 = Hotel.builder()
                .name("DoubleTree by Hilton Minsk")
                .description("Hotel in Minsk")
                .brand("Hilton")
                .address(address1)
                .contacts(contacts1)
                .arrivalTime(arrivalTime1)
                .build();

        Address address2 = Address.builder()
                .houseNumber(10)
                .street("Tverskaya Street")
                .city("Moscow")
                .country("Russia")
                .postCode("101000")
                .build();

        Contacts contacts2 = Contacts.builder()
                .phone("+7 495 123-45-67")
                .email("moscow@hilton.com")
                .build();

        ArrivalTime arrivalTime2 = ArrivalTime.builder()
                .checkIn("15:00")
                .checkOut("12:00")
                .build();

        hotel2 = Hotel.builder()
                .name("Hilton Moscow")
                .description("Hotel in Moscow")
                .brand("Hilton")
                .address(address2)
                .contacts(contacts2)
                .arrivalTime(arrivalTime2)
                .build();

        entityManager.persist(hotel1);
        entityManager.persist(hotel2);
    }

    @Test
    void searchHotels_ByCity_ShouldReturnMatchingHotels() {
        List<Hotel> results = hotelRepository.searchHotels(null, null, "Minsk", null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("DoubleTree by Hilton Minsk");
    }

    @Test
    void searchHotels_ByBrand_ShouldReturnMatchingHotels() {
        List<Hotel> results = hotelRepository.searchHotels(null, "Hilton", null, null, null);

        assertThat(results).hasSize(2);
    }

    @Test
    void searchHotels_ByName_ShouldReturnMatchingHotels() {
        List<Hotel> results = hotelRepository.searchHotels("Moscow", null, null, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Hilton Moscow");
    }

    @Test
    void countByCity_ShouldReturnCorrectCounts() {
        List<Object[]> counts = hotelRepository.countByCity();

        assertThat(counts).hasSize(2);
        assertThat(counts).anyMatch(arr -> arr[0].equals("Minsk") && arr[1].equals(1L));
        assertThat(counts).anyMatch(arr -> arr[0].equals("Moscow") && arr[1].equals(1L));
    }

    @Test
    void countByBrand_ShouldReturnCorrectCounts() {
        List<Object[]> counts = hotelRepository.countByBrand();

        assertThat(counts).hasSize(1);
        assertThat(counts.get(0)[0]).isEqualTo("Hilton");
        assertThat(counts.get(0)[1]).isEqualTo(2L);
    }
}
