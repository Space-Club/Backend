package com.spaceclub.user.repository;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.user.domain.Bookmark;
import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.showEvent;
import static com.spaceclub.event.EventTestFixture.clubEvent;
import static com.spaceclub.user.domain.Status.NOT_REGISTERED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class BookmarkRepositoryTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ClubRepository clubRepository;

    @BeforeEach
    void setUp() {
        Club club = Club.builder()
                .id(1L)
                .name("클럽 명")
                .logoImageUrl("클럽 썸네일 이미지 URL")
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

        Bookmark bookmarkEvent1 = Bookmark.builder()
                .id(1L)
                .user(user)
                .event(event1())
                .build();
        Bookmark bookmarkEvent2 = Bookmark.builder()
                .id(2L)
                .user(user)
                .event(showEvent())
                .build();
        Bookmark bookmarkEvent3 = Bookmark.builder()
                .id(3L)
                .user(user)
                .event(clubEvent())
                .build();
        bookmarkRepository.saveAll(List.of(bookmarkEvent1, bookmarkEvent2, bookmarkEvent3));
    }

    @Test
    void 유저의_북마크_이벤트_조회에_성공한다() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(DESC, "id"));

        // when
        User user = userRepository.findById(1L).orElseThrow();
        Page<Event> bookmarkedEventPages = bookmarkRepository.findBookmarkedEventPages(user, pageRequest);

        // then
        assertAll(
                () -> assertThat(bookmarkedEventPages.getTotalElements())
                        .isEqualTo(3),
                () -> assertThat(bookmarkedEventPages.getContent())
                        .extracting(Event::getId)
                        .containsExactlyInAnyOrder(1L, 2L, 3L)
        );
    }

}
