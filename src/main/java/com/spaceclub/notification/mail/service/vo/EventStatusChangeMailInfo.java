package com.spaceclub.notification.mail.service.vo;

import lombok.Getter;

import static com.spaceclub.notification.mail.domain.Template.*;

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

    public static EventStatusChangeMailInfo of(String email, String clubName, String eventName, String eventStatus) {
        String[] addressArray = email.split(DELIMITER);

        String statusName = ParticipationStatus.getStatusName(eventStatus);
        return new EventStatusChangeMailInfo(addressArray, clubName, eventName, statusName);
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
