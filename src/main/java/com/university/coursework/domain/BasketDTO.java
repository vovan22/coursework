package com.university.coursework.domain;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class BasketDTO {
    UUID id;
    UUID userId;
    List<BasketItemDTO> items;
}