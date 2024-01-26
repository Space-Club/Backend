package com.spaceclub.board.service;

import com.spaceclub.board.controller.domain.Comment;
import com.spaceclub.board.controller.dto.CommentRequest;
import com.spaceclub.board.repository.CommentRepository;
import com.spaceclub.board.service.vo.CommentInfo;
import com.spaceclub.user.service.UserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserProvider userProvider;

    public Page<CommentInfo> getComments(Long postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable)
                .map(comment -> CommentInfo.of(comment, userProvider.getProfile(comment.getAuthorId())));
    }

    public CommentInfo getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();

        return CommentInfo.of(comment, userProvider.getProfile(comment.getAuthorId()));
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
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        comment.updateComment(commentRequest.content(), commentRequest.isPrivate());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

}
