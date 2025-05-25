package com.university.coursework.service.impl;

import com.university.coursework.domain.CommentDTO;
import com.university.coursework.entity.WatchEntity;
import com.university.coursework.entity.CommentEntity;
import com.university.coursework.entity.UserEntity;
import com.university.coursework.exception.WatchNotFoundException;
import com.university.coursework.repository.WatchRepository;
import com.university.coursework.repository.CommentRepository;
import com.university.coursework.repository.UserRepository;
import com.university.coursework.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final WatchRepository watchRepository;
    private final UserRepository userRepository;

    @Override
    public List<CommentDTO> getCommentsByWatchId(UUID watchId) {
        return commentRepository.findByWatchId(watchId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO createComment(CommentDTO commentDTO) {
        WatchEntity watch = watchRepository.findById(commentDTO.getWatchId())
                .orElseThrow(() -> new WatchNotFoundException("Watch not found"));

        UserEntity user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new WatchNotFoundException("User not found"));

        CommentEntity comment = CommentEntity.builder()
                .watch(watch)
                .user(user)
                .rating(commentDTO.getRating())
                .comment(commentDTO.getComment())
                .build();

        CommentEntity savedComment = commentRepository.save(comment);
        return mapToDto(savedComment);
    }

    @Override
    public void deleteComment(UUID commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new WatchNotFoundException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }

    private CommentDTO mapToDto(CommentEntity entity) {
        return CommentDTO.builder()
                .userId(entity.getUser().getId())
                .watchId(entity.getWatch().getId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .build();
    }
}