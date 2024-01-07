package com.spaceclub.board.controller;

import com.spaceclub.board.controller.domain.Comment;
import com.spaceclub.board.controller.dto.CommentRequest;
import com.spaceclub.board.controller.dto.CommentResponse;
import com.spaceclub.board.service.CommentService;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.dto.SliceResponse;
import com.spaceclub.global.jwt.vo.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/v1/boards/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{postId}/comments")
    public SliceResponse<CommentResponse, Comment> getCommentsByPaging(
            @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable,
            @PathVariable Long postId
    ) {
        Slice<Comment> commentPages = commentService.getComments(postId, pageable);
        List<CommentResponse> comments = commentPages.getContent().stream()
                .map(CommentResponse::of)
                .toList();

        return new SliceResponse<>(comments, commentPages);
    }

    @GetMapping("/comments/{commentId}")
    public CommentResponse getSingleComment(
            @PathVariable Long commentId
    ) {
        Comment comment = commentService.getComment(commentId);

        return CommentResponse.of(comment);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Void> createComment(
            @RequestBody CommentRequest commentRequest,
            @PathVariable Long postId,
            @Authenticated JwtUser jwtUser
    ) {
        Long userId = jwtUser.id();
        Long commentId = commentService.createComment(postId, commentRequest, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/boards/posts/comments/%d".formatted(commentId)))
                .build();
    }

    @PutMapping("/comments/{commentId}")
    public void updateComment(
            @RequestBody CommentRequest commentRequest,
            @PathVariable Long commentId
    ) {
        commentService.updateComment(commentId, commentRequest);
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(commentId);
    }

}
