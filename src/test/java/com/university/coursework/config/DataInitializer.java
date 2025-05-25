package com.university.coursework.config;

import com.university.coursework.domain.enums.Role;
import com.university.coursework.entity.*;
import com.university.coursework.repository.*;
import com.university.coursework.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final JwtProvider jwtProvider;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BasketItemRepository basketItemRepository;
    private final BasketRepository basketRepository;
    private final WatchRepository watchRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public TestData initData() {
        orderItemRepository.deleteAll();
        basketItemRepository.deleteAll();
        orderRepository.deleteAll();
        basketRepository.deleteAll();
        commentRepository.deleteAll();
        watchRepository.deleteAll();
        manufacturerRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity user = userRepository.save(UserEntity.builder()
                .email("watchlover@example.com")
                .password("securepassword")
                .username("TimekeeperAdmin")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build());

        String userToken = jwtProvider.createToken(user.getEmail(), user.getRole());

        ManufacturerEntity manufacturer = manufacturerRepository.save(ManufacturerEntity.builder()
                .name("Rolex")
                .description("Legendary Swiss watchmaker known for precision and luxury")
                .logoUrl("/logos/rolex.png")
                .build());

        WatchEntity watch = watchRepository.save(WatchEntity.builder()
                .name("Submariner")
                .description("Iconic luxury dive watch with exceptional craftsmanship")
                .price(BigDecimal.valueOf(9999))
                .stockQuantity(20)
                .manufacturer(manufacturer)
                .build());

        BasketEntity basket = basketRepository.save(BasketEntity.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .build());

        OrderEntity order = orderRepository.save(OrderEntity.builder()
                .user(user)
                .total(BigDecimal.valueOf(19999))
                .status("COMPLETED")
                .address("Lviv")
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build());

        CommentEntity comment = commentRepository.save(CommentEntity.builder()
                .user(user)
                .watch(watch)
                .rating(5)
                .comment("Timeless elegance and superb quality!")
                .build());

        return new TestData(user.getId(), manufacturer.getId(), watch.getId(), basket.getId(), order.getId(), userToken);
    }

    public record TestData(UUID userId, UUID manufacturerId, UUID watchId, UUID basketId, UUID orderId,  String userToken) {}
}
