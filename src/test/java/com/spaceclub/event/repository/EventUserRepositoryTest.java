package com.spaceclub.event.repository;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.domain.ParticipationStatus;
import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spaceclub.event.EventTestFixture.clubEvent;
import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.showEvent;
import static com.spaceclub.user.domain.Status.NOT_REGISTERED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Transactional
@SpringBootTest
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class EventUserRepositoryTest {

    @Autowired
    private EventUserRepository eventUserRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void resetAutoIncrementId() {
        entityManager.createNativeQuery("ALTER TABLE EVENT ALTER COLUMN EVENT_ID RESTART WITH 1")
                .executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE CLUB ALTER COLUMN CLUB_ID RESTART WITH 1")
                .executeUpdate();
    }

    @BeforeEach
    void setUp() {
        Club club = Club.builder()
                .id(1L)
                .name("클럽 명")
                .logoImageName("클럽 썸네일 이미지 URL")
                .info("클럽 정보")
                .build();
        clubRepository.save(club);

        User user = User.builder()
                .id(1L)
                .name("name")
                .phoneNumber("010-1234-5678")
                .oauthId("1234")
                .status(NOT_REGISTERED)
                .provider(Provider.KAKAO)
                .email("abc@naver.com")
                .refreshToken("refreshToken")
                .build();
        userRepository.save(user);

        eventRepository.saveAll(List.of(event1(), showEvent(), clubEvent()));

        EventUser eventUser1 = EventUser.builder()
                .id(1L)
                .userId(1L)
                .event(event1())
                .status(ParticipationStatus.CONFIRMED)
                .build();
        EventUser eventUser2 = EventUser.builder()
                .id(2L)
                .userId(1L)
                .event(showEvent())
                .status(ParticipationStatus.PENDING)
                .build();
        EventUser eventUser3 = EventUser.builder()
                .id(3L)
                .userId(1L)
                .event(clubEvent())
                .status(ParticipationStatus.CANCEL_REQUESTED)
                .build();
        eventUserRepository.saveAll(List.of(eventUser1, eventUser2, eventUser3));
    }

    @Test
    void 유저의_모든_이벤트_조회에_성공한다() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(DESC, "id"));

        // when
        Page<Event> eventPages = eventUserRepository.findAllByUserId(1L, pageRequest);
        List<String> list = eventPages.getContent().stream()
                .map(event -> eventUserRepository.findEventStatusByUserId(1L, event))
                .toList();

        // then
        assertAll(
                () -> assertThat(eventPages.getTotalElements())
                        .isEqualTo(3),
                () -> assertThat(eventPages.getContent())
                        .extracting(Event::getId)
                        .containsExactlyInAnyOrder(1L, 2L, 3L),
                () -> assertThat(list)
                        .containsExactlyInAnyOrder("CONFIRMED", "PENDING", "CANCEL_REQUESTED")
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1,1,true",
            "1,2,true",
            "1,3,true",
            "1,4,false",
            "2,1,false",
    }, delimiter = ',')
    void 유저의_이벤트_존재_여부_확인에_성공한다(Long userId, Long eventId, boolean expected) {
        // when
        boolean exists = eventUserRepository.existsByEventIdAndUserId(eventId, userId);

        // then
        assertThat(exists).isEqualTo(expected);
    }

}
