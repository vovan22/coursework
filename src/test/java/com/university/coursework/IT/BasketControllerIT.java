package com.university.coursework.IT;

import com.university.coursework.config.DataInitializer;
import com.university.coursework.domain.BasketItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.UUID;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasketControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DataInitializer dataInitializer;

    private String userToken;
    private UUID basketId;
    private UUID watchId;
    private UUID userId;

    @BeforeEach
    void setup() {
        DataInitializer.TestData testData = dataInitializer.initData();
        userId = testData.userId();
        watchId = testData.watchId();
        basketId = testData.basketId();
        userToken = testData.userToken();
    }

    @Test
    void shouldAddWatchToBasket() {
        webTestClient.post()
                .uri("/api/v1/baskets/{basketId}", basketId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(BasketItemDTO.builder()
                        .basketId(basketId)
                        .watchId(watchId)
                        .quantity(3)
                        .price(BigDecimal.valueOf(250))
                        .build()
                )
                .exchange()
                .expectStatus().isCreated();

        webTestClient.get()
                .uri("/api/v1/baskets/{userId}", userId)
                .header("Authorization", "Bearer " + userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.items.length()").isEqualTo(1)
                .jsonPath("$.items[0].watchId").isEqualTo(watchId.toString())
                .jsonPath("$.items[0].quantity").isEqualTo(3);
    }

    @Test
    void shouldUpdateWatchQuantityInBasket() {
        webTestClient.post()
                .uri("/api/v1/baskets/{basketId}", basketId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(BasketItemDTO.builder()
                        .basketId(basketId)
                        .watchId(watchId)
                        .quantity(3)
                        .price(BigDecimal.valueOf(250))
                        .build()
                )
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post()
                .uri("/api/v1/baskets/{basketId}", basketId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(BasketItemDTO.builder()
                        .basketId(basketId)
                        .watchId(watchId)
                        .quantity(3)
                        .price(BigDecimal.valueOf(250))
                        .build()
                )
                .exchange()
                .expectStatus().isCreated();

        webTestClient.get()
                .uri("/api/v1/baskets/{userId}", userId)
                .header("Authorization", "Bearer " + userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.items[0].quantity").isEqualTo(6);
    }

    @Test
    void shouldFailToAddWatchToNonexistentBasket() {
        UUID nonexistentBasketId = UUID.randomUUID();

        webTestClient.post()
                .uri("/api/v1/baskets/{basketId}", nonexistentBasketId)
                .header("Authorization", "Bearer " + userToken).
                contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(BasketItemDTO.builder()
                        .basketId(basketId)
                        .watchId(watchId)
                        .quantity(2)
                        .price(BigDecimal.valueOf(200))
                        .build()
                )
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Basket not found with id: " + nonexistentBasketId);
    }

    @Test
    void shouldFailToAddNonexistentWatchToBasket() {
        UUID nonexistentWatchId = UUID.randomUUID();

        webTestClient.post()
                .uri("/api/v1/baskets/{basketId}", basketId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(BasketItemDTO.builder()
                        .basketId(basketId)
                        .watchId(nonexistentWatchId)
                        .quantity(2)
                        .price(BigDecimal.valueOf(100))
                        .build()
                )
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Watch not found with id: " + nonexistentWatchId);
    }
}

