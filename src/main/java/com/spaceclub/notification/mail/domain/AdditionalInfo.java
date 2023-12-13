package com.spaceclub.notification.mail.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class AdditionalInfo {

    private String clubName;

    private String eventName;

    private String eventStatus;

    public AdditionalInfo(String clubName, String eventName, String eventStatus) {
        this.clubName = clubName;
        this.eventName = eventName;
        this.eventStatus = eventStatus;
    }

}
