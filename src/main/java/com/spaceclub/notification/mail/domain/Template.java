package com.spaceclub.notification.mail.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Template {
    WELCOME("Space Club에 가입해주셔서 감사합니다.", "welcome"),
    EVENT_CANCEL_CONFIRMED("Space Club에서 신청하신 이벤트가 취소되었습니다.", "event-status-change"),
    EVENT_APPLY_CONFIRMED("Space Club에서 신청하신 이벤트가 확정되었습니다.", "event-status-change"),
    ;

    private final String title;

    private final String templateName;

}
