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

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Lob
    private String postImageUrl;

    private Long authorId;

    private Long clubId;

    @Builder
    private Post(
            Long id,
            String title,
            String content,
            String postImageUrl,
            Long authorId,
            Long clubId,
            LocalDateTime createdAt,
            LocalDateTime lastModifiedAt)
    {
        this.id = id;
        this.title = title;
        this.content = content;
        this.postImageUrl = postImageUrl;
        this.authorId = authorId;
        this.clubId = clubId;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updatePost(String title, String content, String postImageUrl) {
        this.title = title;
        this.content = content;
        this.postImageUrl = postImageUrl;
    }

}
