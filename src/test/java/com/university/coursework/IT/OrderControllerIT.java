package com.university.coursework.IT;

import com.university.coursework.config.DataInitializer;
import com.university.coursework.domain.BasketItemDTO;
import com.university.coursework.domain.OrderDTO;
import com.university.coursework.entity.BasketEntity;
import com.university.coursework.exception.BasketNotFoundException;
import com.university.coursework.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private UserRepository userRepository;

    private String userToken;
    private UUID userId;
    private UUID basketId;
    private UUID watchId;

    @BeforeEach
    void setup() {
        DataInitializer.TestData testData = dataInitializer.initData();
        watchId = testData.watchId();
        basketId = testData.basketId();
        userId = testData.userId();
        userToken = testData.userToken();
    }


    @Test
    void shouldCreateOrderSuccessfully() {
        assertNotNull(userRepository.findByEmail("watchlover@example.com"), "User not found in DB!");

        webTestClient.post()
                .uri("/api/v1/baskets/{basketId}", basketId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(BasketItemDTO.builder()
                        .basketId(basketId)
                        .watchId(watchId)
                        .quantity(2)
                        .price(BigDecimal.valueOf(100))
                        .build()
                )
                .exchange()
                .expectStatus().isCreated();

        OrderDTO response = webTestClient.post()
                .uri("/api/v1/baskets/{basketId}/checkout?address=Kyiv", basketId)
                .header("Authorization", "Bearer " + userToken)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderDTO.class)
                .returnResult().getResponseBody();

        assertNotNull(response.getId());
        assertEquals("Kyiv", response.getAddress());
        assertEquals(19998, response.getTotal().intValue());

        System.out.println("Checking basket after checkout: " + basketRepository.findById(basketId));

        webTestClient.get()
                .uri("/api/v1/baskets/{userId}", userId)
                .header("Authorization", "Bearer " + userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.items.length()").isEqualTo(0);
    }

    @Test
    @Transactional
    void shouldFailCheckoutWithEmptyBasket() {
        BasketEntity savedBasket = basketRepository.findById(basketId).orElseThrow(() -> new BasketNotFoundException("Basket not found with id = " + basketId));
        assertNotNull(savedBasket, "Basket should exist before checkout");
        assertTrue(savedBasket.getItems() == null || savedBasket.getItems().isEmpty(),
                "Basket should be empty before checkout");

        webTestClient.post()
                .uri("/api/v1/baskets/{basketId}/checkout?address=Kyiv", basketId)
                .header("Authorization", "Bearer " + userToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody();
    }
}