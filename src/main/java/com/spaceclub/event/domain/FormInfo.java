package com.spaceclub.event.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FormInfo {

    private LocalDateTime formOpenDate;

    private LocalDateTime formCloseDate;

    @Builder
    public FormInfo(LocalDateTime formOpenDate, LocalDateTime formCloseDate) {
        this.formOpenDate = formOpenDate;
        this.formCloseDate = formCloseDate;
    }

}
