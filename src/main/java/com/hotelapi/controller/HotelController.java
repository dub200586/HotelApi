package com.hotelapi.controller;

import com.hotelapi.dto.*;
import com.hotelapi.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Hotel Management", description = "Endpoints for managing hotels")
public class HotelController {

    private final HotelService hotelService;

    @GetMapping("/hotels")
    @Operation(summary = "Get all hotels", description = "Returns a list of all hotels with brief information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<HotelSummaryDTO>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/hotels/{id}")
    @Operation(summary = "Get hotel by ID", description = "Returns detailed information about a specific hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved hotel"),
            @ApiResponse(responseCode = "404", description = "Hotel not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<HotelDetailDTO> getHotelById(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search hotels", description = "Search hotels by various parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved search results"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<HotelSummaryDTO>> searchHotels(
            @Parameter(description = "Hotel name to search for")
            @RequestParam(required = false) String name,
            @Parameter(description = "Hotel brand to filter by")
            @RequestParam(required = false) String brand,
            @Parameter(description = "City to filter by")
            @RequestParam(required = false) String city,
            @Parameter(description = "Country to filter by")
            @RequestParam(required = false) String country,
            @Parameter(description = "Amenity to filter by")
            @RequestParam(required = false) String amenity) {
        return ResponseEntity.ok(hotelService.searchHotels(name, brand, city, country, amenity));
    }

    @PostMapping("/hotels")
    @Operation(summary = "Create a new hotel", description = "Creates a new hotel with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hotel successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<HotelSummaryDTO> createHotel(
            @Parameter(description = "Hotel creation data", required = true)
            @Valid
            @RequestBody HotelCreateDTO hotelCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(hotelService.createHotel(hotelCreateDTO));
    }

    @PostMapping("/hotels/{id}/amenities")
    @Operation(summary = "Add amenities to hotel", description = "Adds a list of amenities to an existing hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Amenities successfully added"),
            @ApiResponse(responseCode = "404", description = "Hotel not found"),
            @ApiResponse(responseCode = "400", description = "Invalid amenities data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> addAmenitiesToHotel(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "List of amenities to add", required = true)
            @RequestBody List<String> amenities) {
        hotelService.addAmenitiesToHotel(id, amenities);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/histogram/{param}")
    @Operation(summary = "Get histogram data", description = "Returns histogram data grouped by the specified parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved histogram"),
            @ApiResponse(responseCode = "400", description = "Invalid parameter"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<HistogramResponse> getHistogram(
            @Parameter(description = "Parameter to group by (city, country, brand, amenities)", required = true)
            @PathVariable String param) {
        return ResponseEntity.ok(hotelService.getHistogram(param));
    }
}
