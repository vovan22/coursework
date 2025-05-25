package com.university.coursework.service.impl;

import com.university.coursework.domain.ManufacturerDTO;
import com.university.coursework.entity.ManufacturerEntity;
import com.university.coursework.exception.ManufacturerNotFoundException;
import com.university.coursework.repository.ManufacturerRepository;
import com.university.coursework.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;

    @Override
    public List<ManufacturerDTO> findAllManufacturers() {
        return manufacturerRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ManufacturerDTO findManufacturerById(UUID id) {
        ManufacturerEntity manufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() -> new ManufacturerNotFoundException("Manufacturer not found with id: " + id));
        return mapToDto(manufacturer);
    }

    @Override
    public ManufacturerDTO createManufacturer(ManufacturerDTO manufacturerDTO) {
        ManufacturerEntity manufacturer = mapToEntity(manufacturerDTO);
        ManufacturerEntity savedManufacturer = manufacturerRepository.save(manufacturer);
        return mapToDto(savedManufacturer);
    }

    @Override
    public ManufacturerDTO updateManufacturer(UUID id, ManufacturerDTO manufacturerDTO) {
        ManufacturerEntity existingManufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() -> new ManufacturerNotFoundException("Manufacturer not found with id: " + id));

        ManufacturerEntity updatedManufacturer = ManufacturerEntity.builder()
                .id(existingManufacturer.getId())
                .name(manufacturerDTO.getName())
                .description(manufacturerDTO.getDescription())
                .logoUrl(manufacturerDTO.getLogoUrl())
                .build();

        manufacturerRepository.save(updatedManufacturer);
        return mapToDto(updatedManufacturer);
    }

    @Override
    public void deleteManufacturer(UUID id) {
        if (!manufacturerRepository.existsById(id)) {
            throw new ManufacturerNotFoundException("Manufacturer not found with id: " + id);
        }
        manufacturerRepository.deleteById(id);
    }

    private ManufacturerDTO mapToDto(ManufacturerEntity entity) {
        return ManufacturerDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .logoUrl(entity.getLogoUrl())
                .build();
    }

    private ManufacturerEntity mapToEntity(ManufacturerDTO dto) {
        return ManufacturerEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .logoUrl(dto.getLogoUrl())
                .build();
    }
}