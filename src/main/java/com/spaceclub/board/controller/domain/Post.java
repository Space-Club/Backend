package com.spaceclub.board.controller.domain;

import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String postImageUrl;

    private Long authorId;

    @Builder
    public Post(Long id, String title, String content, String postImageUrl, Long authorId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.postImageUrl = postImageUrl;
        this.authorId = authorId;
    }

}
