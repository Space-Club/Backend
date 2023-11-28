package com.spaceclub.event.domain;

import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.spaceclub.event.EventExceptionMessage.INVALID_EVENT;
import static com.spaceclub.event.EventExceptionMessage.INVALID_EVENT_USER;
import static com.spaceclub.event.domain.ParticipationStatus.CANCELED;
import static com.spaceclub.event.domain.ParticipationStatus.CANCEL_REQUESTED;
import static jakarta.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class EventUser extends BaseTimeEntity {

    @Id
    @Getter
    @Column(name = "event_user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;

    @Getter
    private Long userId;

    @Getter
    @ManyToOne(fetch = EAGER, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private Integer ticketCount;

    @Builder
    private EventUser(Long id, ParticipationStatus status, Long userId, Event event, Integer ticketCount, LocalDateTime createdAt) {
        validate(userId, event);
        this.id = id;
        this.status = status;
        this.userId = userId;
        this.event = event;
        this.ticketCount = ticketCount;
        this.createdAt = createdAt;
    }

    private void validate(Long userId, Event event) {
        Assert.notNull(userId, INVALID_EVENT_USER.toString());
        Assert.notNull(event, INVALID_EVENT.toString());
    }

    public EventUser updateStatus(ParticipationStatus status) {
        return EventUser.builder()
                .id(this.id)
                .status(status)
                .userId(this.userId)
                .event(this.event)
                .ticketCount(this.ticketCount)
                .createdAt(this.createdAt)
                .build();
    }

    public EventUser setStatusByManaged(boolean managed) {
        if (managed) {
            return EventUser.builder()
                    .id(this.id)
                    .status(CANCEL_REQUESTED)
                    .userId(this.userId)
                    .event(this.event)
                    .ticketCount(this.ticketCount)
                    .createdAt(this.createdAt)
                    .build();
        }

        return EventUser.builder()
                .id(this.id)
                .status(CANCELED)
                .userId(this.userId)
                .event(this.event)
                .ticketCount(this.ticketCount)
                .createdAt(this.createdAt)
                .build();
    }

    public Long getEventId() {
        return event.getId();
    }

    public String getParticipationDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return createdAt.format(formatter);
    }

}
