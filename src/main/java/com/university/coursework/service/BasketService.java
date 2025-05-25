package com.university.coursework.service;

import com.university.coursework.domain.BasketDTO;
import com.university.coursework.domain.BasketItemDTO;
import com.university.coursework.domain.OrderDTO;

import java.util.UUID;

public interface BasketService {
    BasketDTO findById(UUID basketId);
    BasketDTO findByUserId(UUID userId);
    BasketItemDTO addItemToBasket(UUID basketId, BasketItemDTO basketItemDTO);
    BasketItemDTO updateBasketItem(UUID itemId, BasketItemDTO basketItemDTO);
    void removeItemFromBasket(UUID itemId);
    OrderDTO checkout(UUID basketId, String address);
}