package com.university.coursework.domain;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class WatchDTO {
    UUID id;
    String name;
    String description;
    UUID manufacturerId;
    BigDecimal price;
    int stockQuantity;
}
