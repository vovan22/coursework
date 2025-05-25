package com.university.coursework.IT;

import com.university.coursework.config.DataInitializer;
import com.university.coursework.domain.WatchDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WatchDetailsIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DataInitializer dataInitializer;

    private UUID watchId;

    @BeforeEach
    void setup() {
        DataInitializer.TestData testData = dataInitializer.initData();
        watchId = testData.watchId();
    }


    @Test
    void shouldRetrieveWatchInfo() {
        webTestClient.get()
                .uri("/api/v1/watches/{watchId}", watchId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WatchDTO.class)
                .consumeWith(response -> {
                    WatchDTO watch = response.getResponseBody();
                    assertNotNull(watch);
                    assertEquals("Submariner", watch.getName());
                    assertEquals("Iconic luxury dive watch with exceptional craftsmanship", watch.getDescription());
                    assertEquals(0, watch.getPrice().compareTo(BigDecimal.valueOf(9999)));
                });
    }

    @Test
    void shouldReturnNotFoundForNonexistentWatch() {
        UUID nonexistentWatchId = UUID.randomUUID();

        webTestClient.get()
                .uri("/api/v1/watches/{watchId}", nonexistentWatchId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Watch not found with id: " + nonexistentWatchId);
    }

    @Test
    void shouldReturnBadRequestForInvalidWatchId() {
        webTestClient.get()
                .uri("/api/v1/watches/b4b07cd1-f5b3-43de-bf62-dba7fcbcd2ce")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Watch not found with id: b4b07cd1-f5b3-43de-bf62-dba7fcbcd2ce");
    }
}
