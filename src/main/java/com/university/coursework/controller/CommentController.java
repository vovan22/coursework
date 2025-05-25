package com.university.coursework.controller;

import com.university.coursework.domain.CommentDTO;
import com.university.coursework.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "APIs for managing watch comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/watch/{watchId}")
    @Operation(summary = "Get comments by watch ID", description = "Retrieves all approved comments for a watch.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Watch not found")
    })
    public ResponseEntity<List<CommentDTO>> getCommentsByWatchId(@Parameter(description = "Watch ID") @PathVariable UUID watchId) {
        return ResponseEntity.ok(commentService.getCommentsByWatchId(watchId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")    @PostMapping
    @Operation(summary = "Add a comment", description = "Allows users to add a comment for a watch.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid comment data")
    })
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        return new ResponseEntity<>(commentService.createComment(commentDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a comment", description = "Deletes a comment from the system. Accessible only by administrators.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<Void> deleteComment(@Parameter(description = "Comment ID") @PathVariable UUID id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
