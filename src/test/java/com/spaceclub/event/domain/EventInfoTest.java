package com.spaceclub.event.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.spaceclub.event.EventTestFixture.eventInfo;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class EventInfoTest {

    private static final String TITLE_MAX_LENGTH = "값".repeat(31);

    private static final String CONTENT_MAX_LENGTH = "값".repeat(201);

    private static final String LOCATION_MAX_LENGTH = "값".repeat(31);

    static Stream<Arguments> invalidTitle() {
        return Stream.of(
                Arguments.arguments(""),
                Arguments.arguments("           "),
                Arguments.arguments(TITLE_MAX_LENGTH)
        );
    }

    static Stream<Arguments> invalidContent() {
        return Stream.of(
                Arguments.arguments(""),
                Arguments.arguments("           "),
                Arguments.arguments(CONTENT_MAX_LENGTH)
        );
    }

    static Stream<Arguments> invalidLocation() {
        return Stream.of(
                Arguments.arguments(""),
                Arguments.arguments("           "),
                Arguments.arguments(LOCATION_MAX_LENGTH)
        );
    }

    @NullSource
    @MethodSource("invalidTitle")
    @ParameterizedTest(name = "{index}. title : [{arguments}]")
    void 제목이_유효하지_않으면_생성에_실패한다(String title) {
        assertThatThrownBy(() ->
                EventInfo.builder()
                        .title(title)
                        .content(eventInfo().getContent())
                        .startDate(eventInfo().getStartDate())
                        .location(eventInfo().getLocation())
                        .capacity(eventInfo().getCapacity())
                        .posterImageUrl(eventInfo().getPosterImageUrl())
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @MethodSource("invalidContent")
    @ParameterizedTest(name = "{index}. content : [{arguments}]")
    void 내용이_유효하지_않으면_생성에_실패한다(String content) {
        assertThatThrownBy(() ->
                EventInfo.builder()
                        .title(eventInfo().getContent())
                        .content(content)
                        .startDate(eventInfo().getStartDate())
                        .location(eventInfo().getLocation())
                        .capacity(eventInfo().getCapacity())
                        .posterImageUrl(eventInfo().getPosterImageUrl())
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @MethodSource("invalidLocation")
    @ParameterizedTest(name = "{index}. location : [{arguments}]")
    void 장소가_유효하지_않으면_생성에_실패한다(String location) {
        assertThatThrownBy(() ->
                EventInfo.builder()
                        .title(eventInfo().getContent())
                        .content(eventInfo().getContent())
                        .startDate(eventInfo().getStartDate())
                        .location(location)
                        .capacity(eventInfo().getCapacity())
                        .posterImageUrl(eventInfo().getPosterImageUrl())
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ValueSource(ints = {0, 1000})
    @ParameterizedTest(name = "{index}. capacity : [{arguments}]")
    void 정원이_유효하지_않으면_생성에_실패한다(int capacity) {
        assertThatThrownBy(() ->
                EventInfo.builder()
                        .title(eventInfo().getContent())
                        .content(eventInfo().getContent())
                        .startDate(eventInfo().getStartDate())
                        .location(eventInfo().getLocation())
                        .capacity(capacity)
                        .posterImageUrl(eventInfo().getPosterImageUrl())
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

}
