package com.university.coursework.service;

import com.university.coursework.domain.WatchDTO;

import java.util.List;
import java.util.UUID;

public interface WatchService {
    List<WatchDTO> getAllWatches();
    WatchDTO getWatchById(UUID id);
    List<WatchDTO> findByManufacturerId(UUID categoryId);
    List<WatchDTO> findAll();
    WatchDTO findById(UUID id);
    WatchDTO createWatch(WatchDTO watchDTO);
    WatchDTO updateWatch(UUID id, WatchDTO watchDTO);
    void deleteWatch(UUID id);
}