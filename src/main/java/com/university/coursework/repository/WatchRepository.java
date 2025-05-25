package com.university.coursework.repository;

import com.university.coursework.entity.WatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface WatchRepository extends JpaRepository<WatchEntity, UUID> {
    List<WatchEntity> findByManufacturerId(UUID manufacturerId);

    @Query("SELECT p FROM WatchEntity p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:manufacturer IS NULL OR LOWER(p.manufacturer.name) LIKE LOWER(CONCAT('%', :manufacturer, '%'))) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<WatchEntity> findByFilters(@Param("name") String name,
                                    @Param("manufacturer") String manufacturer,
                                    @Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice);
}
