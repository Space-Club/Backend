package com.spaceclub.event.repository;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventStatus;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.user.domain.Email;
import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.event2;
import static com.spaceclub.event.EventTestFixture.event3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
@DirtiesContext
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

    @BeforeEach
    void setUp() {
        Club club = Club.builder()
                .id(1L)
                .name("클럽 명")
                .logoImageUrl("클럽 썸네일 이미지 URL")
                .info("클럽 정보")
                .owner("클럽 주인")
                .build();
        clubRepository.save(club);

        User user = User.builder()
                .id(1L)
                .nickname("nickname1")
                .phoneNumber("010-1234-5678")
                .oauthId("1234")
                .provider(Provider.KAKAO)
                .email("abc@naver.com")
                .refreshToken("refreshToken")
                .build();
        userRepository.save(user);

        eventRepository.saveAll(List.of(event1(), event2(), event3()));

        EventUser eventUser1 = EventUser.builder()
                .id(1L)
                .user(user)
                .event(event1())
                .status(EventStatus.CONFIRMED)
                .build();
        EventUser eventUser2 = EventUser.builder()
                .id(2L)
                .user(user)
                .event(event2())
                .status(EventStatus.PENDING)
                .build();
        EventUser eventUser3 = EventUser.builder()
                .id(3L)
                .user(user)
                .event(event3())
                .status(EventStatus.CANCEL_REQUESTED)
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

}
