package com.spaceclub.board.service;

import com.spaceclub.board.controller.domain.Comment;
import com.spaceclub.board.controller.domain.Post;
import com.spaceclub.board.controller.dto.CommentRequest;
import com.spaceclub.board.controller.dto.PostRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    public void updateClubBoardPost(Long postId, PostRequest postRequest, Long userId) {
        // 게시글 수정
    }

    public void deleteClubBoardPost(Long postId, Long userId) {
        // 게시글 삭제
    }

    public Page<Post> getClubBoardPostsByPaging(Pageable pageable, Long clubId, Long id) {
        // 게시글 페이징 조회
        return null;
    }

    public Slice<Comment> getComments(Long postId, Pageable pageable, Long userId) {
        // 댓글 페이징 조회
        return null;
    }

    public Post getClubBoardPost(Long clubId, Long postId, Long userId) {
        // 게시글 단건 조회
        return null;
    }

    public Long createClubBoardPost(Long clubId, PostRequest postRequest, Long userId) {
        // 게시글 생성
        return null;
    }

    public Comment getComment(Long postId, Long commentId, Long userId) {
        // 댓글 단건 조회
        return null;
    }

    public Long createComment(Long postId, CommentRequest commentRequest, Long id) {
        // 댓글 생성
        return null;
    }

    public void updateComment(Long postId, Long commentId, CommentRequest commentRequest, Long userId) {
        // 댓글 수정
    }

    public void deleteComment(Long postId, Long commentId, Long userId) {
        // 댓글 삭제
    }

}
