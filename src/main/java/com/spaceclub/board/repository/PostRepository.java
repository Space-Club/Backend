package com.spaceclub.board.repository;

import com.spaceclub.board.controller.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByClubId(Long clubId, Pageable pageable);

    Optional<Post> findByClubIdAndId(Long clubId, Long postId);

}
