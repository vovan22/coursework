package com.university.coursework.IT;

import com.university.coursework.config.DataInitializer;
import com.university.coursework.domain.CommentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DataInitializer dataInitializer;

    private String userToken;
    private UUID watchId;
    private UUID userId;

    @BeforeEach
    void setup() {
        DataInitializer.TestData testData = dataInitializer.initData();
        userId = testData.userId();
        watchId = testData.watchId();
        userToken = testData.userToken();
    }

    @Test
    void shouldCreateComment() {
        CommentDTO newComment = CommentDTO.builder()
                .userId(userId)
                .watchId(watchId)
                .rating(5)
                .comment("Nice watch.")
                .build();

        webTestClient.post()
                .uri("/api/v1/comments")
                .header("Authorization", "Bearer " + userToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newComment)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CommentDTO.class)
                .consumeWith(response -> {
                    CommentDTO comment = response.getResponseBody();
                    assertNotNull(comment);
                    assertEquals("Nice watch.", comment.getComment());
                    assertEquals(5, comment.getRating());
                });
    }

    @Test
    void shouldGetCommentsByWatchId() {
        webTestClient.get()
                .uri("/api/v1/comments/watch/{watchId}", watchId)
                .header("Authorization", "Bearer " + userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CommentDTO.class)
                .consumeWith(response -> {
                    List<CommentDTO> comments = response.getResponseBody();
                    assertNotNull(comments);
                    assertFalse(comments.isEmpty());
                });
    }

    @Test
    void shouldFailToCreateCommentForNonexistentWatch() {
        CommentDTO newComment = CommentDTO.builder()
                .userId(userId)
                .watchId(UUID.randomUUID())
                .rating(5)
                .comment("Great watch!")
                .build();

        webTestClient.post()
                .uri("/api/v1/comments")
                .header("Authorization", "Bearer " + userToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newComment)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Watch not found");
    }
}
