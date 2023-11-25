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

    @Getter
    private LocalDateTime formOpenDateTime;

    @Getter
    private LocalDateTime formCloseDateTime;

    @Builder
    public FormInfo(LocalDateTime formOpenDateTime, LocalDateTime formCloseDateTime) {
        this.formOpenDateTime = formOpenDateTime;
        this.formCloseDateTime = formCloseDateTime;
    }

}
