package com.university.coursework.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@Table(name = "basket_items")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BasketItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "basket_id", nullable = false)
    private BasketEntity basket;

    @ManyToOne
    @JoinColumn(name = "watch_id", nullable = false)
    private WatchEntity watch;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;
}