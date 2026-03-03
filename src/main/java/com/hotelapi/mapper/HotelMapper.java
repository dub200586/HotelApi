package com.hotelapi.mapper;

import com.hotelapi.dto.*;
import com.hotelapi.model.Address;
import com.hotelapi.model.ArrivalTime;
import com.hotelapi.model.Contacts;
import com.hotelapi.model.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface HotelMapper {
    @Mapping(target = "address", expression = "java(mapAddressToString(hotel))")
    @Mapping(target = "phone", source = "contacts.phone")
    HotelSummaryDTO toSummaryDTO(Hotel hotel);

    List<HotelSummaryDTO> toSummaryDTOList(List<Hotel> hotels);

    @Mapping(target = "amenities", expression = "java(mapAmenitiesToStringList(hotel))")
    HotelDetailDTO toDetailDTO(Hotel hotel);

    Hotel toEntity(HotelCreateDTO dto);

    AddressDTO toAddressDTO(Address address);
    Address toAddress(AddressDTO dto);

    ContactsDTO toContactsDTO(Contacts contacts);
    Contacts toContacts(ContactsDTO dto);

    ArrivalTimeDTO toArrivalTimeDTO(ArrivalTime arrivalTime);
    ArrivalTime toArrivalTime(ArrivalTimeDTO dto);

    @Named("mapAddressToString")
    default String mapAddressToString(Hotel hotel) {
        Address addr = hotel.getAddress();
        return String.format("%d %s, %s, %s, %s",
                addr.getHouseNumber(),
                addr.getStreet(),
                addr.getCity(),
                addr.getPostCode(),
                addr.getCountry());
    }

    @Named("mapAmenitiesToStringList")
    default List<String> mapAmenitiesToStringList(Hotel hotel) {
        if (hotel.getAmenities() == null) {
            return List.of();
        }
        return hotel.getAmenities().stream()
                .map(amenity -> amenity.getName())
                .collect(Collectors.toList());
    }
}
