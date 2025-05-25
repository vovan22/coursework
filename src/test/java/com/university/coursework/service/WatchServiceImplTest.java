package com.university.coursework.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.university.coursework.domain.WatchDTO;
import com.university.coursework.entity.ManufacturerEntity;
import com.university.coursework.entity.WatchEntity;
import com.university.coursework.exception.ManufacturerNotFoundException;
import com.university.coursework.exception.WatchNotFoundException;
import com.university.coursework.repository.ManufacturerRepository;
import com.university.coursework.repository.WatchRepository;
import com.university.coursework.service.impl.WatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class WatchServiceImplTest {

    @Mock
    private WatchRepository watchRepository;
    @Mock
    private ManufacturerRepository manufacturerRepository;

    private WatchServiceImpl watchService;

    private WatchEntity watchEntity;
    private WatchDTO watchDTO;
    private ManufacturerEntity manufacturerEntity;

    private UUID watchId;
    private UUID manufacturerId;

    @BeforeEach
    void setUp() {
        watchService = new WatchServiceImpl(watchRepository, manufacturerRepository);
        watchId = UUID.randomUUID();
        manufacturerId = UUID.randomUUID();

        manufacturerEntity = ManufacturerEntity.builder()
                .id(manufacturerId)
                .name("Rolex")
                .description("Legendary Swiss watchmaker known for precision and luxury")
                .logoUrl("/logos/rolex.png")
                .build();

        watchEntity = WatchEntity.builder()
                .id(watchId)
                .name("Submariner")
                .description("Iconic luxury dive watch with exceptional craftsmanship")
                .price(BigDecimal.valueOf(9999))
                .stockQuantity(20)
                .manufacturer(manufacturerEntity)
                .build();

        watchDTO = WatchDTO.builder()
                .name("Submariner")
                .description("Iconic luxury dive watch with exceptional craftsmanship")
                .price(BigDecimal.valueOf(9999))
                .stockQuantity(20)
                .manufacturerId(manufacturerId)
                .build();
    }

    @Test
    void testGetAllWatches() {
        when(watchRepository.findAll()).thenReturn(List.of(watchEntity));

        List<WatchDTO> result = watchService.getAllWatches();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Submariner", result.get(0).getName());
        verify(watchRepository).findAll();
    }

    @Test
    void testGetWatchById() {
        when(watchRepository.findById(watchId)).thenReturn(Optional.of(watchEntity));

        WatchDTO result = watchService.getWatchById(watchId);

        assertNotNull(result);
        assertEquals("Submariner", result.getName());
        assertEquals(manufacturerId, result.getManufacturerId());
        verify(watchRepository).findById(watchId);
    }

    @Test
    void testGetWatchByIdNotFound() {
        when(watchRepository.findById(watchId)).thenReturn(Optional.empty());

        assertThrows(WatchNotFoundException.class,
                () -> watchService.getWatchById(watchId));
    }

    @Test
    void testFindByManufacturerId() {
        when(watchRepository.findByManufacturerId(manufacturerId)).thenReturn(List.of(watchEntity));

        List<WatchDTO> result = watchService.findByManufacturerId(manufacturerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Submariner", result.get(0).getName());
        verify(watchRepository).findByManufacturerId(manufacturerId);
    }

    @Test
    void testCreateWatch() {
        when(manufacturerRepository.findById(manufacturerId)).thenReturn(Optional.of(manufacturerEntity));
        when(watchRepository.save(any(WatchEntity.class))).thenReturn(watchEntity);

        WatchDTO result = watchService.createWatch(watchDTO);

        assertNotNull(result);
        assertEquals("Submariner", result.getName());
        assertEquals(20, result.getStockQuantity());
        verify(watchRepository).save(any(WatchEntity.class));
    }

    @Test
    void testCreateWatchManufacturerNotFound() {
        when(manufacturerRepository.findById(manufacturerId)).thenReturn(Optional.empty());

        assertThrows(ManufacturerNotFoundException.class,
                () -> watchService.createWatch(watchDTO));
    }

    @Test
    void testUpdateWatch() {
        WatchEntity existingWatch = WatchEntity.builder()
                .id(watchId)
                .name("New Submariner")
                .description("Updated Submariner")
                .manufacturer(manufacturerEntity)
                .price(BigDecimal.valueOf(9999))
                .stockQuantity(20)
                .build();

        when(watchRepository.findById(watchId)).thenReturn(Optional.of(existingWatch));
        when(manufacturerRepository.findById(manufacturerId)).thenReturn(Optional.of(manufacturerEntity));
        when(watchRepository.save(any(WatchEntity.class))).thenAnswer(invocation -> {
            WatchEntity saved = invocation.getArgument(0);
            return saved.toBuilder().id(watchId).build();
        });

        WatchDTO updatedDTO = WatchDTO.builder()
                .name("New Submariner")
                .description("Updated Submariner")
                .manufacturerId(manufacturerId)
                .price(BigDecimal.valueOf(9999))
                .stockQuantity(20)
                .build();

        WatchDTO result = watchService.updateWatch(watchId, updatedDTO);

        assertNotNull(result);
        assertEquals("New Submariner", result.getName());
        assertEquals(BigDecimal.valueOf(9999), result.getPrice());
        verify(watchRepository).save(any(WatchEntity.class));
    }

    @Test
    void testDeleteWatch() {
        when(watchRepository.existsById(watchId)).thenReturn(true);

        watchService.deleteWatch(watchId);

        verify(watchRepository).deleteById(watchId);
    }

    @Test
    void testDeleteWatchNotFound() {
        when(watchRepository.existsById(watchId)).thenReturn(false);

        assertThrows(WatchNotFoundException.class,
                () -> watchService.deleteWatch(watchId));
    }
}