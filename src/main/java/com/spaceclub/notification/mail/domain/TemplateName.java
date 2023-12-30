package com.spaceclub.notification.mail.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TemplateName {

    WELCOME("[Space Club] 스페이스 클럽에 가입해주셔서 감사합니다.", "welcome", 1L),
    EVENT_STATUS_CHANGED("[Space Club] 신청한 행사 상태 변경 알림", "event-status-change", 2L),
    ;

    private final String title;

    private final String templateName;

    private final Long templateId;

    public static TemplateName findByTemplateName(String templateName) {
        return Arrays.stream(values())
                .filter(template -> template.templateName.equals(templateName))
                .findFirst()
                .orElseThrow();
    }

}
