package com.spaceclub.board.service;

import com.spaceclub.board.controller.domain.Comment;
import com.spaceclub.board.controller.dto.CommentRequest;
import com.spaceclub.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment getComment(Long postId, Long commentId, Long userId) {
        return commentRepository.findById(commentId).orElseThrow();
    }

    public Slice<Comment> getComments(Long postId, Pageable pageable, Long userId) {
        return commentRepository.findByPostId(postId, pageable);
    }

    @Transactional
    public Long createComment(Long postId, CommentRequest commentRequest, Long userId) {
        Comment comment = Comment.builder()
                .content(commentRequest.content())
                .authorId(userId)
                .isPrivate(commentRequest.isPrivate())
                .postId(postId)
                .build();

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void updateComment(Long postId, Long commentId, CommentRequest commentRequest, Long userId) {
        Comment comment = getComment(postId, commentId, userId);
        comment.updateComment(commentRequest.content(), commentRequest.isPrivate());
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, Long userId) {
        commentRepository.deleteById(commentId);
    }

}
