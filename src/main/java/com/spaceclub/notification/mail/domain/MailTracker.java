package com.spaceclub.notification.mail.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class MailTracker {

    @Id
    @Getter
    @Column(name = "mail_tracker_id")
    @GeneratedValue(strategy = IDENTITY)
    Long id;

    String addresses;

    String ccAddresses;

    String title;

    String template;

    LocalDateTime sentAt;

    boolean isSent;

    @Builder
    private MailTracker(
            String addresses,
            String ccAddresses,
            String title,
            String template,
            LocalDateTime sentAt,
            boolean isSent
    ) {
        this.addresses = addresses;
        this.ccAddresses = ccAddresses;
        this.title = title;
        this.template = template;
        this.sentAt = sentAt;
        this.isSent = isSent;
    }

}
