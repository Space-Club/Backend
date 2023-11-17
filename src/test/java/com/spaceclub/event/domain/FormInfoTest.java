package com.spaceclub.event.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import static com.spaceclub.event.EventTestFixture.formInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class FormInfoTest {

    @Test
    void 폼_정보_생성에_성공한다() {
        assertThat(formInfo()).isNotNull();
    }

    @Test
    void 폼_오픈_일시가_null이면_생성에_실패한다() {
        assertThatThrownBy(() ->
                FormInfo.builder()
                        .formOpenDateTime(null)
                        .formCloseDateTime(formInfo().getFormCloseDateTime())
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 폼_마감_일시가_null이면_생성에_실패한다() {
        assertThatThrownBy(() ->
                FormInfo.builder()
                        .formOpenDateTime(formInfo().getFormOpenDateTime())
                        .formCloseDateTime(null)
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

}
