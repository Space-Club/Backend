package com.spaceclub.event.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FormInfo {

    @Getter
    private LocalDateTime formOpenDate;

    @Getter
    private LocalDateTime formCloseDate;

    @Builder
    public FormInfo(LocalDateTime formOpenDate, LocalDateTime formCloseDate) {
        validate(formOpenDate, formCloseDate);
        this.formOpenDate = formOpenDate;
        this.formCloseDate = formCloseDate;
    }

    private void validate(LocalDateTime formOpenDate, LocalDateTime formCloseDate) {
        Assert.notNull(formOpenDate, "폼 신청 시작 날짜와 시간은 필수값입니다.");
        Assert.notNull(formCloseDate, "폼 신청 마감 날짜와 시간은 필수값입니다.");
    }

}
