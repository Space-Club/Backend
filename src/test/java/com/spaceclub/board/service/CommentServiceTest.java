package com.spaceclub.board.service;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.board.controller.domain.Comment;
import com.spaceclub.board.controller.dto.CommentRequest;
import com.spaceclub.board.repository.CommentRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    void 게시물의_댓글_페이징_조회에_성공한다() {
        // given
        Long postId = 10L;
        Comment comment1 = Comment.builder()
                .content("comment1")
                .authorId(1L)
                .isPrivate(true)
                .postId(postId)
                .build();
        Comment comment2 = Comment.builder()
                .content("comment2")
                .authorId(5L)
                .isPrivate(true)
                .postId(postId)
                .build();
        Comment comment3 = Comment.builder()
                .content("comment3")
                .authorId(10L)
                .isPrivate(false)
                .postId(postId)
                .build();
        commentRepository.saveAll(List.of(comment1, comment2, comment3));
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "createdAt");

        // when
        Slice<Comment> commentSlice = commentService.getComments(postId, pageRequest);
        List<Comment> comments = commentSlice.getContent();

        // then
        assertAll(
                () -> assertThat(commentSlice).hasSize(3),
                () -> assertThat(commentSlice.isFirst()).isTrue(),
                () -> assertThat(commentSlice.isLast()).isTrue(),
                () -> assertThat(commentSlice.hasNext()).isFalse(),
                () -> assertThat(comments.get(0)).extracting("content").isEqualTo("comment1"),
                () -> assertThat(comments.get(1)).extracting("content").isEqualTo("comment2"),
                () -> assertThat(comments.get(2)).extracting("content").isEqualTo("comment3")
        );
    }

    @Test
    void 댓글_조회에_성공한다() {
        // given
        String content = "comment 1";
        long authorId = 2L;
        long postId = 3L;
        boolean isPrivate = true;
        Comment commentToSave = Comment.builder()
                .content(content)
                .authorId(authorId)
                .isPrivate(isPrivate)
                .postId(postId)
                .build();
        Comment savedComment = commentRepository.save(commentToSave);

        // when
        Comment comment = commentService.getComment(savedComment.getId());

        // then
        assertAll(
                () -> assertThat(comment).extracting("content").isEqualTo(content),
                () -> assertThat(comment).extracting("authorId").isEqualTo(authorId),
                () -> assertThat(comment).extracting("postId").isEqualTo(postId),
                () -> assertThat(comment).extracting("isPrivate").isEqualTo(isPrivate)
        );
    }

    @Test
    void 댓글_생성에_성공한다() {
        // given
        Long userId = 10L;
        Long postId = 5L;
        String content = "content for test";
        boolean isPrivate = true;
        CommentRequest commentRequest = new CommentRequest(content, isPrivate);

        // when
        Long createdCommentId = commentService.createComment(postId, commentRequest, userId);
        Comment comment = commentRepository.findById(createdCommentId).orElseThrow();

        // then
        assertAll(
                () -> assertThat(comment).extracting("content").isEqualTo(content),
                () -> assertThat(comment).extracting("postId").isEqualTo(postId),
                () -> assertThat(comment).extracting("isPrivate").isEqualTo(isPrivate)
        );
    }

    @Test
    void 댓글_수정에_성공한다() {
        // given
        Comment comment = Comment.builder()
                .content("content to update")
                .authorId(1L)
                .isPrivate(true)
                .postId(1L)
                .build();
        Long savedCommentId = commentRepository.save(comment).getId();

        String content = "content updated";
        boolean isPrivate = false;
        CommentRequest commentRequest = new CommentRequest(content, isPrivate);

        // when
        commentService.updateComment(savedCommentId, commentRequest);
        Comment updatedComment = commentRepository.findById(savedCommentId).orElseThrow();

        // then
        assertAll(
                () -> assertThat(updatedComment).extracting("content").isEqualTo(content),
                () -> assertThat(updatedComment).extracting("isPrivate").isEqualTo(isPrivate)
        );
    }

    @Test
    void 댓글_삭제에_성공한다() {
        // given
        Comment comment = Comment.builder()
                .content("content to update")
                .authorId(1L)
                .isPrivate(true)
                .postId(1L)
                .build();
        Long savedCommentId = commentRepository.save(comment).getId();

        // when
        commentService.deleteComment(savedCommentId);

        // then
        assertThatThrownBy(() -> commentRepository.findById(savedCommentId).orElseThrow(IllegalArgumentException::new))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
