package com.university.coursework.service.impl;

import com.university.coursework.domain.WatchDTO;
import com.university.coursework.entity.ManufacturerEntity;
import com.university.coursework.entity.WatchEntity;
import com.university.coursework.exception.ManufacturerNotFoundException;
import com.university.coursework.exception.WatchNotFoundException;
import com.university.coursework.repository.ManufacturerRepository;
import com.university.coursework.repository.WatchRepository;
import com.university.coursework.service.WatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchServiceImpl implements WatchService {

    private final WatchRepository watchRepository;
    private final ManufacturerRepository manufacturerRepository;

    @Override
    public List<WatchDTO> getAllWatches() {
        return watchRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public WatchDTO getWatchById(UUID id) {
        WatchEntity watch = watchRepository.findById(id)
                .orElseThrow(() -> new WatchNotFoundException("Watch not found with id: " + id));
        return mapToDto(watch);
    }

    @Override
    public List<WatchDTO> findByManufacturerId(UUID categoryId) {
        return watchRepository.findByManufacturerId(categoryId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<WatchDTO> findAll() {
        return watchRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public WatchDTO findById(UUID id) {
        WatchEntity watch = watchRepository.findById(id)
                .orElseThrow(() -> new WatchNotFoundException("Watch not found with id: " + id));
        return mapToDto(watch);
    }

    @Override
    public WatchDTO createWatch(WatchDTO watchDTO) {
        ManufacturerEntity manufacturer = manufacturerRepository.findById(watchDTO.getManufacturerId())
                .orElseThrow(() -> new ManufacturerNotFoundException("Manufacturer not found with id: " + watchDTO.getManufacturerId()));

        WatchEntity watch = mapToEntity(watchDTO).toBuilder()
                .manufacturer(manufacturer)
                .build();

        WatchEntity savedWatch = watchRepository.save(watch);
        return mapToDto(savedWatch);
    }

    @Override
    public WatchDTO updateWatch(UUID id, WatchDTO watchDTO) {
        WatchEntity existingWatch = watchRepository.findById(id)
                .orElseThrow(() -> new WatchNotFoundException("Watch not found with id: " + id));

        ManufacturerEntity manufacturer = manufacturerRepository.findById(watchDTO.getManufacturerId())
                .orElseThrow(() -> new WatchNotFoundException("Category not found with id: " + watchDTO.getManufacturerId()));

        WatchEntity updatedWatch = existingWatch.toBuilder()
                .name(watchDTO.getName())
                .description(watchDTO.getDescription())
                .manufacturer(manufacturer)
                .price(watchDTO.getPrice())
                .stockQuantity(watchDTO.getStockQuantity())
                .build();

        WatchEntity savedWatch = watchRepository.save(updatedWatch);
        return mapToDto(savedWatch);
    }

    @Override
    public void deleteWatch(UUID id) {
        if (!watchRepository.existsById(id)) {
            throw new WatchNotFoundException("Watch not found with id: " + id);
        }
        watchRepository.deleteById(id);
    }

    private WatchDTO mapToDto(WatchEntity entity) {
        return WatchDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .manufacturerId(entity.getManufacturer().getId())
                .stockQuantity(entity.getStockQuantity())
                .build();
    }

    private WatchEntity mapToEntity(WatchDTO dto) {
        return WatchEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .build();
    }
}