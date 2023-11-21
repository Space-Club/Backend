package com.spaceclub.event.domain;

import com.spaceclub.club.domain.Club;
import com.spaceclub.form.domain.Form;
import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

    @Id
    @Getter
    @Column(name = "event_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    private EventCategory category;

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
    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<EventUser> eventUsers = new ArrayList<>();

    @Getter
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "form_id")
    private Form form;

    @Builder
    private Event(
            Long id,
            EventCategory category,
            EventInfo eventInfo,
            BankInfo bankInfo,
            TicketInfo ticketInfo,
            FormInfo formInfo,
            Club club,
            Form form,
            LocalDateTime createdAt
    ) {
        Assert.notNull(category, "행사 카테고리는 필수 값입니다.");
        this.id = id;
        this.category = category;
        this.eventInfo = eventInfo;
        this.bankInfo = bankInfo;
        this.ticketInfo = ticketInfo;
        this.formInfo = formInfo;
        this.club = club;
        this.form = form;
        this.createdAt = createdAt;
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
                .createdAt(this.createdAt)
                .build();
    }

    public Event registerForm(Form form) {
        return Event.builder()
                .id(this.id)
                .category(this.category)
                .eventInfo(this.eventInfo)
                .bankInfo(this.bankInfo)
                .ticketInfo(this.ticketInfo)
                .formInfo(this.formInfo)
                .club(this.club)
                .form(form)
                .createdAt(this.createdAt)
                .build();
    }

    public Event registerPosterImage(String posterImageName) {
        return Event.builder()
                .id(this.id)
                .category(this.category)
                .eventInfo(this.eventInfo.registerPosterImage(posterImageName))
                .bankInfo(this.bankInfo)
                .ticketInfo(this.ticketInfo)
                .formInfo(this.formInfo)
                .club(this.club)
                .form(this.form)
                .createdAt(this.createdAt)
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
        if (eventInfo.getStartDateTime() == null) return null;
        return eventInfo.getStartDateTime().toLocalDate();
    }

    public LocalTime getStartTime() {
        if (eventInfo.getStartDateTime() == null) return null;
        return eventInfo.getStartDateTime().toLocalTime();
    }

    public String getLocation() {
        if (eventInfo.getLocation() == null) return null;
        return eventInfo.getLocation();
    }

    public Long getClubId() {
        return club.getId();
    }

    public String getClubName() {
        return club.getName();
    }

    public String getClubLogoImageUrl() {
        return club.getLogoImageUrl();
    }

    public LocalDateTime getFormOpenDateTime() {
        return formInfo.getFormOpenDateTime();
    }

    public LocalDateTime getFormCloseDateTime() {
        return formInfo.getFormCloseDateTime();
    }

    public LocalDate getFormOpenDate() {
        return formInfo.getFormOpenDateTime().toLocalDate();
    }

    public LocalDate getFormCloseDate() {
        if (formInfo.getFormCloseDateTime() == null) return null;
        return formInfo.getFormCloseDateTime().toLocalDate();
    }

    public LocalTime getFormOpenTime() {
        return formInfo.getFormOpenDateTime().toLocalTime();
    }

    public LocalTime getFormCloseTime() {
        if (formInfo.getFormCloseDateTime() == null) return null;
        return formInfo.getFormCloseDateTime().toLocalTime();
    }

    public Long getFormId() {
        return form.getId();
    }

    public String getContent() {
        return this.eventInfo.getContent();
    }

    public String getManagerName() {
        return this.eventInfo.getManagerName();
    }

    public boolean isFormManaged() {
        if (form == null || !form.isManaged()) {
            return false;

        }
        return form.isManaged();
    }

    public boolean isNotFormManaged() {
        return !this.isFormManaged();
    }

    public Integer getMaxTicketCount() {
        return ticketInfo.getMaxTicketCount();
    }

    public Integer getDues() {
        return this.eventInfo.getDues();
    }

    public Integer getCapacity() {
        return this.eventInfo.getCapacity();
    }

    public String getRecruitmentTarget() {
        return this.eventInfo.getRecruitmentTarget();
    }

    public String getActivityArea() {
        return this.eventInfo.getActivityArea();
    }

    public Integer getCost() {
        return this.ticketInfo.getCost();
    }

    public LocalDate getEndDate() {
        return this.eventInfo.getEndDateTime().toLocalDate();
    }

    public LocalTime getEndTime() {
        return this.eventInfo.getEndDateTime().toLocalTime();
    }

    public String getBankName() {
        return this.bankInfo.getBankName();
    }

    public String getBankAccountNumber() {
        return this.bankInfo.getBankAccountNumber();
    }

}
