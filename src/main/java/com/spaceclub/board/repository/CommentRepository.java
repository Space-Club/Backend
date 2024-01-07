package com.spaceclub.board.repository;

import com.spaceclub.board.controller.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Slice<Comment> findByPostId(Long postId, Pageable pageable);

}
