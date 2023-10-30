package com.spaceclub.event.domain;

import com.spaceclub.global.BaseTimeEntity;
import com.spaceclub.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class EventUser extends BaseTimeEntity {

    @Id
    @Column(name = "event_user_id")
    @GeneratedValue
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @ManyToOne(fetch = LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Builder
    private EventUser(Long id, EventStatus status, User user, Event event) {
        validate(user, event);
        this.id = id;
        this.status = status;
        this.user = user;
        this.event = event;
    }

    private void validate(User user, Event event) {
        Assert.notNull(user, "유저는 필수입니다.");
        Assert.notNull(event, "이벤트는 필수입니다.");
    }

}
