package com.spaceclub.event.domain;

import com.spaceclub.club.domain.Club;
import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseTimeEntity {

    @Id
    @Getter
    @Column(name = "event_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Getter
    @Embedded
    private EventInfo eventInfo;

    @Embedded
    private BankInfo bankInfo;

    @Embedded
    private TicketInfo ticketInfo;

    @Embedded
    private FormInfo formInfo;

    @Getter
    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @Getter
    @OneToMany(mappedBy = "event")
    private List<EventUser> users = new ArrayList<>();

    @Builder
    private Event(
            Long id,
            Category category,
            EventInfo eventInfo,
            BankInfo bankInfo,
            TicketInfo ticketInfo,
            FormInfo formInfo,
            Club club
    ) {
        this.id = id;
        this.category = category;
        this.eventInfo = eventInfo;
        this.bankInfo = bankInfo;
        this.ticketInfo = ticketInfo;
        this.formInfo = formInfo;
        this.club = club;
    }

    public Event registerClub(Club club) {
        return Event.builder()
                .id(this.id)
                .category(this.category)
                .eventInfo(this.eventInfo)
                .bankInfo(this.bankInfo)
                .ticketInfo(this.ticketInfo)
                .formInfo(this.formInfo)
                .club(club)
                .build();
    }

    public String getTitle() {
        return eventInfo.getTitle();
    }

    public String getPoster() {
        return eventInfo.getPoster();
    }

    public LocalDate getStartDate() {
        return eventInfo.getStartDate().toLocalDate();
    }

    public LocalTime getStartTime() {
        return eventInfo.getStartDate().toLocalTime();
    }

    public String getLocation() {
        return eventInfo.getLocation();
    }

    public String getClubName() {
        return club.getName();
    }

    public String getClubImage() {
        return club.getImage();
    }

}
