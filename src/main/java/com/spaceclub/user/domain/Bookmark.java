package com.spaceclub.user.domain;

import com.spaceclub.event.domain.Event;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import static jakarta.persistence.FetchType.EAGER;
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

    private boolean bookmarkStatus;

    @ManyToOne(fetch = EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = EAGER, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Builder
    private Bookmark(Long id, boolean bookmarkStatus, User user, Event event) {
        validate(user, event);
        this.id = id;
        this.bookmarkStatus = bookmarkStatus;
        this.user = user;
        this.event = event;
    }

    private void validate(User user, Event event) {
        Assert.notNull(user, "유저는 필수입니다.");
        Assert.notNull(event, "이벤트는 필수입니다.");
    }

    public Bookmark changeBookmarkStatus(boolean bookmarkStatus) {
        this.bookmarkStatus = bookmarkStatus;
        return this;
    }

}
