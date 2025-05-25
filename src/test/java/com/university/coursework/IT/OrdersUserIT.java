package com.university.coursework.IT;

import com.university.coursework.config.DataInitializer;
import com.university.coursework.domain.OrderDTO;
import com.university.coursework.domain.enums.Role;
import com.university.coursework.entity.UserEntity;
import com.university.coursework.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrdersUserIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private UserRepository userRepository;

    private UUID userId;
    private UUID orderId;
    private String userToken;

    @BeforeEach
    void setup() {
        DataInitializer.TestData testData = dataInitializer.initData();
        userId = testData.userId();
        orderId = testData.orderId();
        userToken = testData.userToken();
    }

    @Test
    void shouldRetrieveOrderHistoryForUser() {
        webTestClient.get()
                .uri("/api/v1/orders/user/{userId}", userId)
                .header("Authorization", "Bearer " + userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(orderId.toString())
                .jsonPath("$[0].total").isEqualTo(19999)
                .jsonPath("$[0].status").isEqualTo("COMPLETED")
                .jsonPath("$[0].address").isEqualTo("Lviv");
    }

    @Test
    void shouldReturnEmptyOrderHistoryForUserWithoutOrders() {
        UserEntity newUser = userRepository.save(UserEntity.builder()
                .email("newuser@example.com")
                .password("password")
                .username("newuser")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build());
        UUID newUserId = newUser.getId();

        webTestClient.get()
                .uri("/api/v1/orders/user/{userId}", newUserId)
                .header("Authorization", "Bearer " + userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void shouldRetrieveOrderDetails() {
        webTestClient.get()
                .uri("/api/v1/orders/user/{userId}", userId)
                .header("Authorization", "Bearer " + userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OrderDTO.class)
                .consumeWith(response -> {
                    List<OrderDTO> orders = response.getResponseBody();
                    assertNotNull(orders);
                    assertFalse(orders.isEmpty(), "Order history should not be empty");
                    assertEquals(0, orders.get(0).getTotal().compareTo(BigDecimal.valueOf(19999)));                });
    }
}
