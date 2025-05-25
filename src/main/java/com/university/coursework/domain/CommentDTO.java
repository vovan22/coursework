package com.university.coursework.domain;

import lombok.*;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class CommentDTO {
    UUID userId;
    UUID watchId;
    Integer rating;
    String comment;
}