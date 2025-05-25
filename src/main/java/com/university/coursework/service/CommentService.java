package com.university.coursework.service;

import com.university.coursework.domain.CommentDTO;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    List<CommentDTO> getCommentsByWatchId(UUID watchId);
    CommentDTO createComment(CommentDTO commentDTO);
    void deleteComment(UUID commentId);
}