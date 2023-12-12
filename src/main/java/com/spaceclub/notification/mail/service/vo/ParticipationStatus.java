package com.spaceclub.notification.mail.service.vo;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ParticipationStatus {

    PENDING("대기"),
    CONFIRMED("확정"),
    CANCEL_REQUESTED("취소 요청"),
    CANCELED("취소");

    private final String statusName;

    public static String getStatusName(String eventStatus) {
        return Arrays.stream(values())
                .filter(status -> status.statusName.equals(eventStatus))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트 상태입니다."))
                .statusName;
    }

}
