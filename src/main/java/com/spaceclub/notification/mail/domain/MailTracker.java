package com.spaceclub.notification.mail.domain;

import com.spaceclub.notification.mail.service.vo.EventStatusChangeMailInfo;
import com.spaceclub.notification.mail.service.vo.MailInfo;
import com.spaceclub.notification.mail.service.vo.Template;
import com.spaceclub.notification.mail.service.vo.WelcomeMailInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
public class MailTracker {

    @Id
    @Getter
    @Column(name = "mail_tracker_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Getter
    private String addresses;

    @Getter
    private String title;

    @Getter
    private String templateName;

    @Getter
    private Long templateId;

    @Embedded
    private AdditionalInfo additionalInfo;

    private LocalDateTime sentAt;

    private boolean isSent;

    @Builder
    private MailTracker(
            String addresses,
            String title,
            String template,
            LocalDateTime sentAt,
            boolean isSent,
            AdditionalInfo additionalInfo
    ) {
        this.addresses = addresses;
        this.title = title;
        this.templateName = template;
        this.templateId = setTemplateId(template);
        this.sentAt = sentAt;
        this.isSent = isSent;
        log.info("MailTracker created: {}", this);
    }

    public static MailTracker from(MailInfo mailInfo, boolean isSent) {
        if (mailInfo instanceof EventStatusChangeMailInfo eventStatusChangeMailInfo) {
            return MailTracker.builder()
                    .addresses(String.join(",", mailInfo.email()))
                    .title(mailInfo.title())
                    .template(mailInfo.templateName())
                    .sentAt(LocalDateTime.now())
                    .isSent(isSent)
                    .additionalInfo(
                            new AdditionalInfo(eventStatusChangeMailInfo.getClubName(),
                                    eventStatusChangeMailInfo.getEventName(),
                                    eventStatusChangeMailInfo.getEventStatus()
                            )
                    )
                    .build();
        }
        return MailTracker.builder()
                .addresses(String.join(",", mailInfo.email()))
                .title(mailInfo.title())
                .template(mailInfo.templateName())
                .sentAt(LocalDateTime.now())
                .isSent(isSent)
                .build();
    }

    private Long setTemplateId(String template) {
        Template foundTemplate = Template.findByTemplateName(template);

        return switch (foundTemplate) {
            case WELCOME -> 1L;
            case EVENT_STATUS_CHANGED -> 2L;
        };
    }

    public void changeToSent() {
        this.sentAt = LocalDateTime.now();
        this.isSent = true;
    }

    public MailInfo toMailInfo() {
        Template template = Template.findByTemplateName(templateName);

        return switch (template) {
            case WELCOME -> WelcomeMailInfo.from(addresses);
            case EVENT_STATUS_CHANGED -> EventStatusChangeMailInfo.of(addresses, additionalInfo.getClubName(), additionalInfo.getEventName(), additionalInfo.getEventStatus());
        };
    }

}
