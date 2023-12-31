package com.spaceclub.event.repository;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.event.domain.Event;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.club.ClubTestFixture.club2;
import static com.spaceclub.event.EventTestFixture.clubEvent;
import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.showEvent;
import static com.spaceclub.user.UserTestFixture.user1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ClubRepository clubRepository;

    @Test
    void 클럽의_모든_이벤트_조회에_성공한다() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(DESC, "id"));
        clubRepository.saveAll(List.of(club1(), club2()));
        eventRepository.saveAll(List.of(event1(), showEvent(), clubEvent().registerClubAndUser(club2(), user1().getId())));

        // when
        Page<Event> eventPages = eventRepository.findByClub_Id(club1().getId(), pageRequest);
        List<Event> events = eventPages.getContent();

        // then
        assertThat(eventPages.getTotalElements()).isEqualTo(2);
        events.forEach(event -> assertThat(event.getClub()).isEqualTo(club1()));
    }

}
