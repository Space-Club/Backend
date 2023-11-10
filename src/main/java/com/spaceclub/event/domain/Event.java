package com.spaceclub.event.domain;

import com.spaceclub.club.domain.Club;
import com.spaceclub.form.domain.Form;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Event extends BaseTimeEntity {

    private static final String EVENT_POSTER_S3_URL = "https://space-club-image-bucket.s3.ap-northeast-2.amazonaws.com/event-poster/";

    private static final String CLUB_LOGO_S3_URL = "https://space-club-image-bucket.s3.ap-northeast-2.amazonaws.com/club-logo/";

    @Id
    @Getter
    @Column(name = "event_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Getter
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

    @OneToMany(mappedBy = "event")
    private List<Form> forms = new ArrayList<>();

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
        Assert.notNull(category, "행사 카테고리는 필수 값입니다.");
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

    public String getPosterImageUrl() {
        if (eventInfo.getPosterImageUrl() == null) {
            return null;
        }
        return EVENT_POSTER_S3_URL + eventInfo.getPosterImageUrl();
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

    public Long getClubId() {
        return club.getId();
    }

    public String getClubName() {
        return club.getName();
    }

    public String getClubLogoImageUrl() {
        if (club.getLogoImageUrl() == null) {
            return null;
        }
        return CLUB_LOGO_S3_URL + club.getLogoImageUrl();
    }

    public LocalDateTime getFormOpenDate() {
        return formInfo.getFormOpenDate();
    }

    public LocalDateTime getFormCloseDate() {
        return formInfo.getFormCloseDate();
    }

}
