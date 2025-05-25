package com.university.coursework.IT;

import com.university.coursework.config.DataInitializer;
import com.university.coursework.domain.WatchDTO;
import com.university.coursework.repository.WatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WatchControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private WatchRepository watchRepository;

    private String userToken;
    private UUID watchId;
    private UUID manufacturerId;


    @BeforeEach
    void setup() {
        DataInitializer.TestData testData = dataInitializer.initData();
        watchId = testData.watchId();
        manufacturerId = testData.manufacturerId();
        userToken = testData.userToken();
    }

    @Test
    void shouldCreateNewWatch() {
        WatchDTO newWatch = WatchDTO.builder()
                .name("Top watches")
                .description("Very expensive watches")
                .price(BigDecimal.valueOf(3000))
                .manufacturerId(manufacturerId)
                .stockQuantity(5)
                .build();

        webTestClient.post()
                .uri("/api/v1/watches")
                .header("Authorization", "Bearer " + userToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newWatch)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(WatchDTO.class)
                .consumeWith(response -> {
                    WatchDTO watch = response.getResponseBody();
                    assertNotNull(watch);
                    assertEquals("Top watches", watch.getName());
                    assertEquals("Very expensive watches", watch.getDescription());
                    assertEquals(BigDecimal.valueOf(3000), watch.getPrice());
                });
    }

    @Test
    void shouldUpdateWatchDetails() {
        WatchDTO updatedWatch = WatchDTO.builder()
                .name("Updated Top watches")
                .description("Even more expensive watches")
                .price(BigDecimal.valueOf(29999))
                .manufacturerId(manufacturerId)
                .stockQuantity(6)
                .build();

        webTestClient.put()
                .uri("/api/v1/watches/{watchId}", watchId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updatedWatch)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WatchDTO.class)
                .consumeWith(response -> {
                    WatchDTO watch = response.getResponseBody();
                    assertNotNull(watch);
                    assertEquals("Updated Top watches", watch.getName());
                    assertEquals("Even more expensive watches", watch.getDescription());
                    assertEquals(BigDecimal.valueOf(29999), watch.getPrice());
                });
    }

    @Test
    void shouldReturnNotFoundForUpdatingNonexistentWatch() {
        UUID nonexistentWatchId = UUID.randomUUID();

        WatchDTO updatedWatch = WatchDTO.builder()
                .name("Strange watches")
                .description("Very strange watches")
                .price(BigDecimal.valueOf(4000))
                .manufacturerId(manufacturerId)
                .stockQuantity(6)
                .build();

        webTestClient.put()
                .uri("/api/v1/watches/{watchId}", nonexistentWatchId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updatedWatch)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Watch not found with id: " + nonexistentWatchId);
    }

    @Test
    void shouldReturnNotFoundForDeletingNonexistentWatch() {
        UUID nonexistentWatchId = UUID.randomUUID();

        webTestClient.delete()
                .uri("/api/v1/watches/{watchId}", nonexistentWatchId)
                .header("Authorization", "Bearer " + userToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Watch not found with id: " + nonexistentWatchId);
    }
}