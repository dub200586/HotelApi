package com.hotelapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelCreateDTO {

    @NotBlank(message = "Hotel name is required")
    private String name;

    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotNull(message = "Address is required")
    @Valid
    private AddressDTO address;

    @NotNull(message = "Contacts are required")
    @Valid
    private ContactsDTO contacts;

    @NotNull(message = "Arrival time is required")
    @Valid
    private ArrivalTimeDTO arrivalTime;
}
