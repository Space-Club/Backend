package com.spaceclub.club.service;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubNotice;
import com.spaceclub.club.repository.ClubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class ClubServiceTest {

    @InjectMocks
    private ClubService clubService;

    @Mock
    private ClubRepository clubRepository;

    private Club club;

    @BeforeEach
    void setup() {
        club = Club.builder()
                .id(1L)
                .name("연사모")
                .info("연어를 사랑하는 모임")
                .owner("연어대장")
                .thumbnailUrl("연어.jpg")
                .notices(List.of(new ClubNotice("연어 공지사항 1: 연어는 맛있어요")))
                .build();
    }

    @Test
    void 클럽_조회에_성공한다() {
        // given
        Long id = 1L;
        given(clubRepository.save(any(Club.class))).willReturn(club);
        given(clubRepository.findById(id)).willReturn(Optional.of(club));

        clubRepository.save(club);

        // when
        Club myClub = clubService.getClub(id);

        // then
        assertThat(club).usingRecursiveComparison().isEqualTo(myClub);
    }

    @Test
    void 클럽_삭제에_성공한다() {
        // given
        Long id = 1L;

        // when
        clubService.deleteClub(id);

        // then
        assertThatThrownBy(() -> clubService.getClub(id))
                .isInstanceOf(IllegalArgumentException.class);

    }

}
