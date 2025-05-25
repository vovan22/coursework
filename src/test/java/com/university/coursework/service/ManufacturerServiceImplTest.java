package com.university.coursework.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.university.coursework.domain.ManufacturerDTO;
import com.university.coursework.entity.ManufacturerEntity;
import com.university.coursework.exception.ManufacturerNotFoundException;
import com.university.coursework.repository.ManufacturerRepository;
import com.university.coursework.service.impl.ManufacturerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ManufacturerServiceImplTest {

    @Mock
    private ManufacturerRepository manufacturerRepository;

    private ManufacturerServiceImpl manufacturerService;

    private UUID manufacturerId;
    private ManufacturerEntity manufacturerEntity;
    private ManufacturerDTO manufacturerDTO;

    @BeforeEach
    void setUp() {
        manufacturerService = new ManufacturerServiceImpl(manufacturerRepository);
        manufacturerId = UUID.randomUUID();

        manufacturerEntity = ManufacturerEntity.builder()
                .id(manufacturerId)
                .name("Rolex")
                .description("Legendary Swiss watchmaker known for precision and luxury")
                .logoUrl("/logos/rolex.png")
                .build();

        manufacturerDTO = ManufacturerDTO.builder()
                .id(manufacturerId)
                .name("Rolex")
                .description("Legendary Swiss watchmaker known for precision and luxury")
                .logoUrl("/logos/rolex.png")
                .build();
    }

    @Test
    void testFindAllManufacturers() {
        when(manufacturerRepository.findAll()).thenReturn(List.of(manufacturerEntity));

        List<ManufacturerDTO> result = manufacturerService.findAllManufacturers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Rolex", result.get(0).getName());
        verify(manufacturerRepository).findAll();
    }

    @Test
    void testFindManufacturerById() {
        when(manufacturerRepository.findById(manufacturerId)).thenReturn(Optional.of(manufacturerEntity));

        ManufacturerDTO result = manufacturerService.findManufacturerById(manufacturerId);

        assertNotNull(result);
        assertEquals(manufacturerId, result.getId());
        assertEquals("Legendary Swiss watchmaker known for precision and luxury", result.getDescription());
        verify(manufacturerRepository).findById(manufacturerId);
    }

    @Test
    void testFindManufacturerByIdNotFound() {
        when(manufacturerRepository.findById(manufacturerId)).thenReturn(Optional.empty());
        assertThrows(ManufacturerNotFoundException.class, () -> manufacturerService.findManufacturerById(manufacturerId));
    }

    @Test
    void testCreateManufacturer() {
        when(manufacturerRepository.save(any(ManufacturerEntity.class))).thenReturn(manufacturerEntity);

        ManufacturerDTO result = manufacturerService.createManufacturer(manufacturerDTO);

        assertNotNull(result);
        assertEquals("Rolex", result.getName());
        verify(manufacturerRepository).save(any(ManufacturerEntity.class));
    }

    @Test
    void testUpdateManufacturer() {
        ManufacturerEntity existingManufacturer = ManufacturerEntity.builder()
                .id(manufacturerId)
                .name("Rolex")
                .description("Legendary Swiss watchmaker known for precision and luxury")
                .logoUrl("/logos/rolex.png")
                .build();

        when(manufacturerRepository.findById(manufacturerId)).thenReturn(Optional.of(existingManufacturer));
        when(manufacturerRepository.save(any(ManufacturerEntity.class))).thenReturn(manufacturerEntity);

        ManufacturerDTO updatedDTO = ManufacturerDTO.builder()
                .id(manufacturerId)
                .name("New Rolex")
                .description("New watchmaker.")
                .logoUrl("/new-logo.png")
                .build();

        ManufacturerDTO result = manufacturerService.updateManufacturer(manufacturerId, updatedDTO);

        assertNotNull(result);
        assertEquals("New Rolex", result.getName());
        assertEquals("New watchmaker.", result.getDescription());
        verify(manufacturerRepository).save(any(ManufacturerEntity.class));
    }

    @Test
    void testDeleteManufacturer() {
        when(manufacturerRepository.existsById(manufacturerId)).thenReturn(true);
        manufacturerService.deleteManufacturer(manufacturerId);
        verify(manufacturerRepository).deleteById(manufacturerId);
    }

    @Test
    void testDeleteManufacturerNotFound() {
        when(manufacturerRepository.existsById(manufacturerId)).thenReturn(false);
        assertThrows(ManufacturerNotFoundException.class, () -> manufacturerService.deleteManufacturer(manufacturerId));
    }
}
