package com.spaceclub.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class Bookmark {

    @Id
    @Getter
    @Column(name = "bookmark_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long userId;

    private Long eventId;

    @Builder
    private Bookmark(Long id, Long userId, Long eventId) {
        validate(userId, eventId);
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
    }

    private void validate(Long userId, Long eventId) {
        if (userId == null || eventId == null) {
            throw new IllegalArgumentException("userId나 eventId가 null입니다.");
        }
    }

}
