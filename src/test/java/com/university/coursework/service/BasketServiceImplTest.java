package com.university.coursework.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.university.coursework.domain.BasketDTO;
import com.university.coursework.domain.BasketItemDTO;
import com.university.coursework.domain.OrderDTO;
import com.university.coursework.entity.*;
import com.university.coursework.repository.BasketItemRepository;
import com.university.coursework.repository.BasketRepository;
import com.university.coursework.repository.WatchRepository;
import com.university.coursework.repository.UserRepository;
import com.university.coursework.service.impl.BasketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class BasketServiceImplTest {

    @Mock
    private BasketRepository basketRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WatchRepository watchRepository;
    @Mock
    private BasketItemRepository basketItemRepository;
    @Mock
    private OrderService orderService;

    private BasketServiceImpl basketService;
    private UUID basketId, basketPartId, userId, basketItemId;
    private BasketEntity basketEntity;
    private WatchEntity watchEntity;
    private BasketItemEntity basketItemEntity;
    private BasketItemDTO basketItemDTO;

    @BeforeEach
    void setUp() {

        Mockito.framework().clearInlineMock(this);

        basketService = new BasketServiceImpl(basketRepository, watchRepository, userRepository,
                basketItemRepository, orderService);

        userId = UUID.randomUUID();
        basketId = UUID.randomUUID();
        basketPartId = UUID.randomUUID();
        basketItemId = UUID.randomUUID();

        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .email("user@example.com")
                .build();

        ManufacturerEntity manufacturerEntity = ManufacturerEntity.builder()
                .id(UUID.randomUUID())
                .name("Rolex")
                .build();

        watchEntity = WatchEntity.builder()
                .id(basketPartId)
                .name("Submariner")
                .price(BigDecimal.valueOf(9999))
                .manufacturer(manufacturerEntity)
                .stockQuantity(10)
                .build();

        basketEntity = BasketEntity.builder()
                .id(basketId)
                .user(userEntity)
                .build();

        basketItemEntity = BasketItemEntity.builder()
                .id(basketItemId)
                .basket(basketEntity)
                .watch(watchEntity)
                .quantity(3)
                .price(watchEntity.getPrice())
                .build();

        basketItemDTO = BasketItemDTO.builder()
                .basketId(basketId)
                .watchId(basketPartId)
                .quantity(3)
                .build();
    }

    @Test
    void testFindByUserId() {
        when(basketRepository.findByUserId(userId)).thenReturn(Optional.of(basketEntity));
        when(basketItemRepository.findByBasketId(basketId)).thenReturn(List.of(basketItemEntity));

        BasketDTO result = basketService.findByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(1, result.getItems().size());
        verify(basketRepository).findByUserId(userId);
    }

    @Test
    void testAddItemToBasket() {
        when(basketRepository.findById(basketId)).thenReturn(Optional.of(basketEntity));
        when(watchRepository.findById(basketPartId)).thenReturn(Optional.of(watchEntity));
        when(basketItemRepository.findByBasketIdAndWatchId(basketId, basketPartId))
                .thenReturn(Optional.empty());
        when(basketItemRepository.save(any(BasketItemEntity.class))).thenReturn(basketItemEntity);

        BasketItemDTO result = basketService.addItemToBasket(basketId, basketItemDTO);

        assertNotNull(result);
        assertEquals(basketPartId, result.getWatchId());
        verify(basketItemRepository).save(any(BasketItemEntity.class));
    }

    @Test
    void testUpdateBasketItem() {
        when(basketItemRepository.findById(basketItemId)).thenReturn(Optional.of(basketItemEntity));
        when(basketItemRepository.save(any(BasketItemEntity.class))).thenReturn(basketItemEntity);

        BasketItemDTO updatedDTO = BasketItemDTO.builder()
                .quantity(5)
                .build();

        BasketItemDTO result = basketService.updateBasketItem(basketItemId, updatedDTO);

        assertNotNull(result);
        assertEquals(3, result.getQuantity());
    }

    @Test
    void testRemoveItemFromBasket() {
        when(basketItemRepository.existsById(basketItemId)).thenReturn(true);

        basketService.removeItemFromBasket(basketItemId);

        verify(basketItemRepository).deleteById(basketItemId);
    }

    @Test
    void testCheckout() {
        when(basketRepository.findById(basketId)).thenReturn(Optional.of(basketEntity));
        when(basketItemRepository.findByBasketId(basketId)).thenReturn(List.of(basketItemEntity));
        when(orderService.createOrderFromBasket(any(), any(), any()))
                .thenReturn(OrderDTO.builder().id(UUID.randomUUID()).build());

        OrderDTO result = basketService.checkout(basketId, "Test Address");

        assertNotNull(result);
        assertNotNull(result.getId());
    }
}