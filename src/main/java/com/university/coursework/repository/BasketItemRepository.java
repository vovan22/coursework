package com.university.coursework.repository;

import com.university.coursework.entity.BasketItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BasketItemRepository extends JpaRepository<BasketItemEntity, UUID> {
    List<BasketItemEntity> findByBasketId(UUID basketId);
    Optional<BasketItemEntity> findByBasketIdAndWatchId(UUID basketId, UUID watchId);
    void deleteByBasketId(UUID basketId);
}