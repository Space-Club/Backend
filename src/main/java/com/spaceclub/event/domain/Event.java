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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.spaceclub.event.EventExceptionMessage.EVENT_CATEGORY_NOT_NULL;
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
    private Long userId;

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
            Long userId,
            LocalDateTime createdAt
    ) {
        if (category == null) throw new IllegalArgumentException(EVENT_CATEGORY_NOT_NULL.toString());
        this.id = id;
        this.category = category;
        this.eventInfo = eventInfo;
        this.bankInfo = bankInfo;
        this.ticketInfo = ticketInfo;
        this.formInfo = formInfo;
        this.club = club;
        this.form = form;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public EventBuilder event() {
        return new EventBuilder()
                .id(this.id)
                .category(this.category)
                .eventInfo(this.eventInfo)
                .bankInfo(this.bankInfo)
                .ticketInfo(this.ticketInfo)
                .formInfo(this.formInfo)
                .club(this.club)
                .form(this.form)
                .userId(this.userId)
                .createdAt(this.createdAt);
    }

    public Event registerClubAndUser(Club club, Long userId) {
        return event()
                .club(club)
                .userId(userId)
                .build();
    }

    public Event registerForm(Form form) {
        return event()
                .form(form)
                .build();
    }

    public Event registerPosterImage(String posterImageName) {
        return event()
                .eventInfo(this.eventInfo.registerPosterImage(posterImageName))
                .build();
    }

    public Event registerParticipants(int participants) {
        return event()
                .eventInfo(this.eventInfo.registerApplicants(participants))
                .build();
    }

    public Event update(Event event) {
        return event()
                .id(event.id)
                .category(event.category)
                .eventInfo(event.eventInfo)
                .bankInfo(event.bankInfo)
                .ticketInfo(event.ticketInfo)
                .formInfo(event.formInfo)
                .build();
    }

    public boolean isEventEnded() {
        if (this.getEndDate() == null) {
            return LocalDateTime.now().isAfter(this.getStartDate().atTime(this.getStartTime()));
        }

        return LocalDateTime.now().isAfter(this.getEndDate().atTime(this.getEndTime()));
    }

    public String getTitle() {
        return eventInfo.getTitle();
    }

    public String getPosterImageName() {
        return eventInfo.getPosterImageName();
    }

    public LocalDate getStartDate() {
        if (eventInfo.getStartDateTime() == null) return null;
        return eventInfo.getStartDateTime().toLocalDate();
    }

    public LocalTime getStartTime() {
        if (eventInfo.getStartDateTime() == null) return null;
        return eventInfo.getStartDateTime().toLocalTime();
    }

    public LocalDateTime getStartDateTime() {
        return eventInfo.getStartDateTime();
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

    public String getClubLogoImageName() {
        return club.getLogoImageName();
    }

    public String getClubCoverImageName() {
        return club.getCoverImageName();
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

    public boolean isFormed() {
        return form != null;
    }

    public boolean isFormManaged() {
        return form != null && form.isManaged();
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
        if (this.eventInfo.getEndDateTime() == null) return null;

        return this.eventInfo.getEndDateTime().toLocalDate();
    }

    public LocalTime getEndTime() {
        if (this.eventInfo.getEndDateTime() == null) return null;

        return this.eventInfo.getEndDateTime().toLocalTime();
    }

    public String getBankName() {
        if (this.bankInfo == null) return null;

        return this.bankInfo.getBankName();
    }

    public Integer getRecruitmentLimit() {
        return this.eventInfo.getRecruitmentLimit();
    }

    public String getBankAccountNumber() {
        if (this.bankInfo == null) return null;

        return this.bankInfo.getBankAccountNumber();
    }

    public boolean hasForm() {
        return this.form != null;
    }

    public Boolean isAbleToApply() {
        if (this.formInfo.getFormOpenDateTime() == null) return null;
        if (this.formInfo.getFormCloseDateTime() == null) return null;

        LocalDateTime now = LocalDateTime.now();

        return this.formInfo.getFormOpenDateTime().isBefore(now)
                && this.formInfo.getFormCloseDateTime().isAfter(now);
    }

    public int getParticipants() {
        return this.eventInfo.getParticipants();
    }

}
