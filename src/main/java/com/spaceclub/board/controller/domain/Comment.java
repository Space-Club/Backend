package com.spaceclub.board.controller.domain;

import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    private Long authorId;

    private boolean isPrivate;

    private Long postId;

    @Builder
    private Comment(Long id, String content, Long authorId, boolean isPrivate, Long postId) {
        this.id = id;
        this.content = content;
        this.authorId = authorId;
        this.isPrivate = isPrivate;
        this.postId = postId;
    }

    public void updateComment(String content, boolean isPrivate) {
        this.content = content;
        this.isPrivate = isPrivate;
    }

}
