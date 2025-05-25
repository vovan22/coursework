package com.university.coursework.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.university.coursework.domain.CommentDTO;
import com.university.coursework.domain.enums.Role;
import com.university.coursework.entity.WatchEntity;
import com.university.coursework.entity.CommentEntity;
import com.university.coursework.entity.UserEntity;
import com.university.coursework.exception.WatchNotFoundException;
import com.university.coursework.repository.WatchRepository;
import com.university.coursework.repository.CommentRepository;
import com.university.coursework.repository.UserRepository;
import com.university.coursework.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private WatchRepository watchRepository;
    @Mock
    private UserRepository userRepository;

    private CommentServiceImpl commentService;

    private UUID commentId;
    private UUID watchId;
    private UUID userId;
    private CommentEntity commentEntity;
    private CommentDTO commentDTO;
    private WatchEntity watchEntity;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(commentRepository, watchRepository, userRepository);
        commentId = UUID.randomUUID();
        watchId = UUID.randomUUID();
        userId = UUID.randomUUID();

        userEntity = UserEntity.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        watchEntity = WatchEntity.builder()
                .id(watchId)
                .name("Submariner")
                .description("Iconic luxury dive watch with exceptional craftsmanship")
                .price(BigDecimal.valueOf(9999))
                .stockQuantity(50)
                .createdAt(LocalDateTime.now())
                .build();


        commentEntity = CommentEntity.builder()
                .id(commentId)
                .user(userEntity)
                .watch(watchEntity)
                .rating(5)
                .comment("Nice watch.")
                .build();


        commentDTO = CommentDTO.builder()
                .userId(userId)
                .watchId(watchId)
                .rating(5)
                .comment("Nice watch.")
                .build();
    }

    @Test
    void testGetCommentsByWatchId() {
        when(commentRepository.findByWatchId(watchId)).thenReturn(List.of(commentEntity));

        List<CommentDTO> result = commentService.getCommentsByWatchId(watchId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository).findByWatchId(watchId);
    }

    @Test
    void testCreateComment() {
        when(watchRepository.findById(watchId)).thenReturn(Optional.of(watchEntity));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(commentEntity);

        CommentDTO result = commentService.createComment(commentDTO);

        assertNotNull(result);
        assertEquals("Nice watch.", result.getComment());
        verify(commentRepository).save(any(CommentEntity.class));
    }

    @Test
    void testDeleteComment() {
        when(commentRepository.existsById(commentId)).thenReturn(true);

        commentService.deleteComment(commentId);

        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void testDeleteCommentNotFound() {
        when(commentRepository.existsById(commentId)).thenReturn(false);

        assertThrows(WatchNotFoundException.class, () -> commentService.deleteComment(commentId));
    }
}
