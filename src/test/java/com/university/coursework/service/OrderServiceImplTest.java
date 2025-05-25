package com.university.coursework.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.university.coursework.domain.OrderDTO;
import com.university.coursework.entity.*;
import com.university.coursework.exception.OrderNotFoundException;
import com.university.coursework.repository.BasketItemRepository;
import com.university.coursework.repository.OrderItemRepository;
import com.university.coursework.repository.OrderRepository;
import com.university.coursework.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private BasketItemRepository basketItemRepository;

    private OrderServiceImpl orderService;

    private UUID orderId;
    private UUID basketId;
    private UUID userId;
    private UUID basketItemId;
    private UUID watchId;
    private BasketEntity basketEntity;
    private OrderEntity orderEntity;
    private WatchEntity watchEntity;
    private BasketItemEntity basketItemEntity;
    private OrderItemEntity orderItemEntity;


    @BeforeEach
    void setUp() {
        Mockito.framework().clearInlineMock(this);

        orderService = new OrderServiceImpl(orderRepository, orderItemRepository, basketItemRepository);
        orderId = UUID.randomUUID();
        basketId = UUID.randomUUID();
        userId = UUID.randomUUID();
        basketItemId = UUID.randomUUID();
        watchId = UUID.randomUUID();

        watchEntity = WatchEntity.builder()
                .id(watchId)
                .name("Test Watch")
                .price(BigDecimal.valueOf(100))
                .build();

        basketEntity = BasketEntity.builder()
                .id(basketId)
                .user(new UserEntity())
                .build();

        basketItemEntity = BasketItemEntity.builder()
                .id(basketItemId)
                .basket(basketEntity)
                .watch(watchEntity)
                .quantity(2)
                .price(watchEntity.getPrice())
                .build();

        orderEntity = OrderEntity.builder()
                .id(orderId)
                .user(basketEntity.getUser())
                .total(BigDecimal.valueOf(100))
                .status("CREATED")
                .address("Test Address")
                .items(new ArrayList<>())
                .build();

        orderItemEntity = OrderItemEntity.builder()
                .id(UUID.randomUUID())
                .watch(watchEntity)
                .order(orderEntity)
                .quantity(2)
                .price(watchEntity.getPrice())
                .build();
    }

    @Test
    void testCreateOrderFromBasket() {
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity order = invocation.getArgument(0);
            return order.toBuilder()
                    .id(orderId)
                    .items(order.getItems().stream()
                            .map(item -> item.toBuilder().id(UUID.randomUUID()).build())
                            .toList())
                    .build();
        });
        when(orderItemRepository.saveAll(any())).thenReturn(List.of(orderItemEntity));

        OrderDTO result = orderService.createOrderFromBasket(basketEntity, List.of(basketItemEntity), "Test Address");

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
    }

    @Test
    void testFindAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(orderEntity));

        List<OrderDTO> result = orderService.findAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void testFindOrdersByUserId() {
        when(orderRepository.findByUserId(userId)).thenReturn(List.of(orderEntity));

        List<OrderDTO> result = orderService.findOrdersByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findByUserId(userId);
    }

    @Test
    void testFindOrderById() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));

        OrderDTO result = orderService.findOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void testFindOrderByIdNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.findOrderById(orderId));
    }

    @Test
    void testUpdateOrderStatus() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        OrderDTO result = orderService.updateOrderStatus(orderId, "SHIPPED");

        assertNotNull(result);
        assertEquals("SHIPPED", result.getStatus());
        verify(orderRepository).save(any(OrderEntity.class));
    }
}
