package com.university.coursework.controller;

import com.university.coursework.domain.ManufacturerDTO;
import com.university.coursework.service.ManufacturerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manufacturers")
@RequiredArgsConstructor
@Tag(name = "Manufacturers", description = "APIs for managing product manufacturers")
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @GetMapping
    @Operation(summary = "Get all manufacturers", description = "Retrieves the list of available manufacturers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manufacturers retrieved successfully")
    })
    public ResponseEntity<List<ManufacturerDTO>> getAllManufacturers() {
        return ResponseEntity.ok(manufacturerService.findAllManufacturers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get manufacturer by ID", description = "Retrieves details of a specific manufacturer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manufacturer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Manufacturer not found")
    })
    public ResponseEntity<ManufacturerDTO> getManufacturerById(@Parameter(description = "Manufacturer ID") @PathVariable UUID id) {
        return ResponseEntity.ok(manufacturerService.findManufacturerById(id));
    }

    @PostMapping
    @Operation(summary = "Create new manufacturer", description = "Adds a new manufacturer to the system.")
    public ResponseEntity<ManufacturerDTO> createManufacturer(@RequestBody ManufacturerDTO manufacturerDTO) {
        return new ResponseEntity<>(manufacturerService.createManufacturer(manufacturerDTO), HttpStatus.CREATED);
    }
}