package com.spaceclub.notification.mail.service.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Template {

    WELCOME("[Space Club] 스페이스 클럽에 가입해주셔서 감사합니다.", "welcome"),
    EVENT_STATUS_CHANGED("[Space Club] 신청한 행사 상태 변경 알림", "event-status-change"),
    ;

    private final String title;

    private final String templateName;

    public static Template findByTemplateName(String templateName) {
        return Arrays.stream(values())
                .filter(template -> template.templateName.equals(templateName))
                .findFirst()
                .orElseThrow();
    }
}
