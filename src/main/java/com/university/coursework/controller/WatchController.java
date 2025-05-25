package com.university.coursework.controller;

import com.university.coursework.domain.WatchDTO;
import com.university.coursework.service.WatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/watches")
@RequiredArgsConstructor
@Tag(name = "Watches", description = "APIs for managing watches")
public class WatchController {

    private final WatchService watchService;

    @GetMapping
    @Operation(summary = "Get all watches", description = "Retrieves a list of all available watches.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "watches retrieved successfully")
    })
    public ResponseEntity<List<WatchDTO>> getAllWatches() {
        return ResponseEntity.ok(watchService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get watch by ID", description = "Retrieves a watch by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "watch retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "watch not found")
    })
    public ResponseEntity<WatchDTO> getWatchById(@Parameter(description = "watch ID") @PathVariable UUID id) {
        return ResponseEntity.ok(watchService.findById(id));
    }

    @GetMapping("/manufacturer/{manufacturerId}")
    @Operation(summary = "Get watches by manufacturer", description = "Retrieves all watches of a specific manufacturer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car parts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Manufacturer not found")
    })
    public ResponseEntity<List<WatchDTO>> getWatchesByManufacturer(@Parameter(description = "Manufacturer ID") @PathVariable UUID manufacturerId) {
        return ResponseEntity.ok(watchService.findByManufacturerId(manufacturerId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new watch", description = "Adds a new watch to the system. Accessible only by administrators.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Car part created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid watch data")
    })
    public ResponseEntity<WatchDTO> createWatch(@RequestBody WatchDTO watchDTO) {
        return new ResponseEntity<>(watchService.createWatch(watchDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a watch", description = "Updates an existing watch. Accessible only by administrators.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car part updated successfully"),
            @ApiResponse(responseCode = "404", description = "Car part not found")
    })
    public ResponseEntity<WatchDTO> updateWatch(@Parameter(description = "watch ID") @PathVariable UUID id, @RequestBody WatchDTO watchDTO) {
        return ResponseEntity.ok(watchService.updateWatch(id, watchDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a watch", description = "Deletes a watch from the system. Accessible only by administrators.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Car part deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Car part not found")
    })
    public ResponseEntity<Void> deleteWatch(@Parameter(description = "watch ID") @PathVariable UUID id) {
        watchService.deleteWatch(id);
        return ResponseEntity.noContent().build();
    }
}
