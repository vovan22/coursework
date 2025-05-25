package com.university.coursework.service;

import com.university.coursework.domain.ManufacturerDTO;
import java.util.List;
import java.util.UUID;

public interface ManufacturerService {
    List<ManufacturerDTO> findAllManufacturers();
    ManufacturerDTO findManufacturerById(UUID id);
    ManufacturerDTO createManufacturer(ManufacturerDTO manufacturerDTO);
    ManufacturerDTO updateManufacturer(UUID id, ManufacturerDTO manufacturerDTO);
    void deleteManufacturer(UUID id);
}