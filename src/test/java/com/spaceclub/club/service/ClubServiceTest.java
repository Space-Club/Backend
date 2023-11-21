package com.spaceclub.club.service;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.user.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.club.ClubUserTestFixture.club1User1Manager;
import static com.spaceclub.user.UserTestFixture.user1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Disabled
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class ClubServiceTest {

    @InjectMocks
    private ClubService clubService;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private ClubUserRepository clubUserRepository;

    @Mock
    private UserRepository userRepository;

    private final ClubUserUpdate clubUserUpdate = ClubUserUpdate.builder()
            .clubId(club1().getId())
            .memberId(user1().getId())
            .role(ClubUserRole.MEMBER)
            .build();

    @Test
    void 클럽_조회에_성공한다() {
        // given
        Long id = 1L;
        given(clubRepository.save(any(Club.class))).willReturn(club1());
        given(clubRepository.findById(id)).willReturn(Optional.of(club1()));

        clubRepository.save(club1());

        // when
        Club myClub = clubService.getClub(id, user1().getId());

        // then
        assertThat(club1()).usingRecursiveComparison().isEqualTo(myClub);
    }

    @Test
    void 클럽_삭제에_성공한다() {
        // given
        Long clubId = 1L;
        Long userId = 1L;
        ClubUser clubUser = club1User1Manager();

        given(clubUserRepository.findByClub_IdAndUserId(clubId, userId)).willReturn(Optional.of(clubUser));

        // when
        clubService.deleteClub(clubId, userId);

        // then
        assertThatThrownBy(() -> clubService.getClub(clubId, user1().getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
