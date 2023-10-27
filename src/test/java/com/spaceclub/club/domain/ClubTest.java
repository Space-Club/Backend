package com.spaceclub.club.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class ClubTest {

    @ValueSource(strings = {"내 클럽", "연사모", "오마이클럽", "연어를 사랑하는 모임"})
    @ParameterizedTest(name = "{index}. name: {arguments}")
    void 양끝_공백을_제거한_12글자_이하의_이름을_가진_클럽_생성에_성공한다(String validName) {
        // when, then
        assertThatNoException()
                .isThrownBy(() -> Club.builder()
                        .name(validName)
                        .build());
    }

    @ValueSource(strings = {" ", "  ", "          "})
    @ParameterizedTest(name = "{index}. name: {arguments}")
    @NullAndEmptySource
    void 이름이_null_이거나_공백으로만_이루어진_경우_해당_이름을_가진_클럽_생성에_실패한다(String invalidName) {
        assertThatThrownBy(() -> Club.builder()
                .name(invalidName)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ValueSource(strings = {"아기다리고기다리던나만의클럽", "세상에 마상에 이렇게 멋진 이름이라니", "피카츄 라이츄 파이리 꼬부기"})
    @ParameterizedTest(name = "{index}. name: {arguments}")
    void 이름이_12글자가_넘어가는_경우_해당_이름을_가진_클럽_생성에_실패한다(String invalidName) {
        assertThatThrownBy(() -> Club.builder()
                .name(invalidName)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

}
