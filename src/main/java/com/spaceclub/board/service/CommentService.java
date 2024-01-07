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

    public Slice<Comment> getComments(Long postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow();
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
    public void updateComment(Long commentId, CommentRequest commentRequest) {
        Comment comment = getComment(commentId);
        comment.updateComment(commentRequest.content(), commentRequest.isPrivate());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

}
