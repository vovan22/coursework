package com.university.coursework.repository;

import com.university.coursework.entity.ManufacturerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ManufacturerRepository extends JpaRepository<ManufacturerEntity, UUID> {
    ManufacturerEntity findByName(String name);
}