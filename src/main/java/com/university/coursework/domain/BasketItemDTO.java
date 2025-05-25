package com.university.coursework.domain;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class BasketItemDTO {
    UUID basketId;
    UUID watchId;
    Integer quantity;
    BigDecimal price;
}