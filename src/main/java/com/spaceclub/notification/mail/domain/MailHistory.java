package com.spaceclub.notification.mail.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Slf4j
@Entity
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class MailHistory {

    @Id
    @Getter
    @Column(name = "mail_history_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Getter
    private String addresses;

    @Getter
    private String title;

    @Getter
    @Enumerated(EnumType.STRING)
    private TemplateName templateName;

    @Getter
    private Long templateId;

    @Embedded
    private AdditionalInfo additionalInfo;

    private LocalDateTime sentAt;

    private boolean isSent;

    @Builder
    private MailHistory(
            String addresses,
            String title,
            TemplateName templateName,
            LocalDateTime sentAt,
            boolean isSent,
            AdditionalInfo additionalInfo
    ) {
        this.addresses = addresses;
        this.title = title;
        this.templateName = templateName;
        this.templateId = templateName.getTemplateId();
        this.sentAt = sentAt;
        this.isSent = isSent;
        log.info("MailTracker created: {}", this);
    }

    public void changeToSent() {
        this.sentAt = LocalDateTime.now();
        this.isSent = true;
    }

    public String getClubName() {
        return additionalInfo.getClubName();
    }

    public String getEventName() {
        return additionalInfo.getEventName();
    }

    public String getEventStatus() {
        return additionalInfo.getEventStatus();
    }

}
