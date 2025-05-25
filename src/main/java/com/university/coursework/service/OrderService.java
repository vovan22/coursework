package com.university.coursework.service;

import com.university.coursework.domain.OrderDTO;
import com.university.coursework.entity.BasketEntity;
import com.university.coursework.entity.BasketItemEntity;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDTO createOrderFromBasket(BasketEntity basket, List<BasketItemEntity> basketItems, String address);
    List<OrderDTO> findAllOrders();
    List<OrderDTO> findOrdersByUserId(UUID userId);
    OrderDTO findOrderById(UUID id);
    OrderDTO updateOrderStatus(UUID id, String status);
}