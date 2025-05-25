package com.university.coursework.service.impl;

import com.university.coursework.domain.BasketDTO;
import com.university.coursework.domain.BasketItemDTO;
import com.university.coursework.domain.OrderDTO;
import com.university.coursework.entity.*;
import com.university.coursework.exception.BasketNotFoundException;
import com.university.coursework.exception.EmptyBasketException;
import com.university.coursework.exception.WatchNotFoundException;
import com.university.coursework.exception.UserNotFoundException;
import com.university.coursework.repository.BasketItemRepository;
import com.university.coursework.repository.BasketRepository;
import com.university.coursework.repository.WatchRepository;
import com.university.coursework.repository.UserRepository;
import com.university.coursework.service.BasketService;
import com.university.coursework.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasketServiceImpl implements BasketService {

    private final BasketRepository basketRepository;
    private final WatchRepository watchRepository;
    private final UserRepository userRepository;
    private final BasketItemRepository basketItemRepository;
    private final OrderService orderService;

    @Override
    public BasketDTO findByUserId(UUID userId) {
        Optional<BasketEntity> basketOpt = basketRepository.findByUserId(userId);
        if(basketOpt.isEmpty()) {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
            BasketEntity basket = basketRepository.save(
                    BasketEntity.builder()
                            .user(user)
                            .items(new ArrayList<>())
                            .build()
            );
            return mapBasketToDto(basket, new ArrayList<>());
        }
        List<BasketItemEntity> items = basketItemRepository.findByBasketId(basketOpt.get().getId());
        return mapBasketToDto(basketOpt.get(), items);
    }

    @Override
    public BasketDTO findById(UUID basketId) {
        BasketEntity basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException("Basket not found with id: " + basketId));

        List<BasketItemEntity> items = basketItemRepository.findByBasketId(basket.getId());
        return mapBasketToDto(basket, items);
    }

    @Override
    public BasketItemDTO addItemToBasket(UUID basketId, BasketItemDTO basketItemDTO) {
        BasketEntity basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException("Basket not found with id: " + basketId));

        WatchEntity watch = watchRepository.findById(basketItemDTO.getWatchId())
                .orElseThrow(() -> new WatchNotFoundException("Watch not found with id: " + basketItemDTO.getWatchId()));

        Optional<BasketItemEntity> existingItem = basketItemRepository.findByBasketIdAndWatchId(basketId, basketItemDTO.getWatchId());

        BasketItemEntity basketItem;
        if (existingItem.isPresent()) {
            basketItem = existingItem.get();
            basketItem = basketItem.toBuilder()
                    .quantity(existingItem.get().getQuantity() + basketItemDTO.getQuantity())
                    .build();
        } else {
            basketItem = BasketItemEntity.builder()
                    .basket(basket)
                    .watch(watch)
                    .quantity(basketItemDTO.getQuantity())
                    .price(watch.getPrice())
                    .build();
        }

        BasketItemEntity savedItem = basketItemRepository.save(basketItem);
        return mapBasketItemToDto(savedItem);
    }

    @Override
    public BasketItemDTO updateBasketItem(UUID itemId, BasketItemDTO basketItemDTO) {
        BasketItemEntity basketItem = basketItemRepository.findById(itemId)
                .orElseThrow(() -> new WatchNotFoundException("Basket item not found with id: " + itemId));

        basketItem = basketItem.toBuilder()
                .quantity(basketItemDTO.getQuantity())
                .build();
        BasketItemEntity updatedItem = basketItemRepository.save(basketItem);
        return mapBasketItemToDto(updatedItem);
    }

    @Override
    public void removeItemFromBasket(UUID itemId) {
        if (!basketItemRepository.existsById(itemId)) {
            throw new WatchNotFoundException("Basket item not found with id: " + itemId);
        }
        basketItemRepository.deleteById(itemId);
    }

    @SneakyThrows
    @Override
    public OrderDTO checkout(UUID basketId, String address) {
        BasketEntity basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException("Basket not found with id: " + basketId));


        List<BasketItemEntity> basketItems = basketItemRepository.findByBasketId(basketId);
        if (basketItems.isEmpty()) {
            throw new EmptyBasketException("Basket is empty, cannot proceed to checkout");
        }

        if (basket.getItems() != null) {
            basket.getItems().clear();
        }

        basketRepository.save(basket);

        return orderService.createOrderFromBasket(basket, basketItems, address);
    }

    private BasketDTO mapBasketToDto(BasketEntity basket, List<BasketItemEntity> items) {
        return BasketDTO.builder()
                .id(basket.getId())
                .userId(basket.getUser().getId())
                .items(items.stream()
                        .map(this::mapBasketItemToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private BasketItemDTO mapBasketItemToDto(BasketItemEntity entity) {
        return BasketItemDTO.builder()
                .basketId(entity.getBasket().getId())
                .watchId(entity.getWatch().getId())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .build();
    }
}