package com.spaceclub.notification.mail.service.vo;

import com.spaceclub.notification.mail.domain.AdditionalInfo;
import com.spaceclub.notification.mail.domain.MailTracker;
import com.spaceclub.notification.mail.domain.TemplateName;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.spaceclub.notification.mail.domain.TemplateName.*;

@Getter
public class EventStatusChangeMailInfo extends MailInfo {

    private static final String DELIMITER = ",";

    private final String clubName;
    private final String eventName;
    private final String eventStatus;

    private EventStatusChangeMailInfo(String[] addresses, String clubName, String eventName, String eventStatus) {
        super(addresses);
        this.clubName = clubName;
        this.eventName = eventName;
        this.eventStatus = eventStatus;
    }

    public static EventStatusChangeMailInfo from(String email, String clubName, String eventName, String eventStatus) {
        String[] addressArray = email.split(DELIMITER);
        String statusName = ParticipationStatus.getStatusName(eventStatus);

        return new EventStatusChangeMailInfo(addressArray, clubName, eventName, statusName);
    }

    public static EventStatusChangeMailInfo from(MailTracker mailTracker) {
        String[] addressArray = mailTracker.getAddresses().split(DELIMITER);
        String statusName = ParticipationStatus.getStatusName(mailTracker.getEventStatus());

        return new EventStatusChangeMailInfo(addressArray, mailTracker.getClubName(), mailTracker.getEventName(), statusName);
    }

    public static MailTracker from(MailInfo mailInfo, boolean isSent) {
        EventStatusChangeMailInfo eventStatusChangeMailInfo = (EventStatusChangeMailInfo) mailInfo;

        return MailTracker.builder()
                .addresses(String.join(DELIMITER, mailInfo.email()))
                .title(mailInfo.title())
                .templateName(TemplateName.findByTemplateName(mailInfo.templateName()))
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

    @Override
    public String title() {
        return EVENT_STATUS_CHANGED.getTitle();
    }

    @Override
    public String templateName() {
        return EVENT_STATUS_CHANGED.getTemplateName();
    }

}
