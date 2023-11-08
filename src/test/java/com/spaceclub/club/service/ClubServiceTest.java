package com.spaceclub.club.service;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubNotice;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.club.ClubUserTestFixture.club1User1Manager;
import static com.spaceclub.user.UserTestFixture.user1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
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

    @Mock
    private ClubUserRepository clubUserRepository;

    private final ClubUserUpdate clubUserUpdate = ClubUserUpdate.builder()
            .clubId(club1().getId())
            .memberId(user1().getId())
            .role(ClubUserRole.MEMBER)
            .build();

    private Club club;

    @BeforeEach
    void setup() {
        club = Club.builder()
                .id(1L)
                .name("연사모")
                .info("연어를 사랑하는 모임")
                .logoImageUrl("연어.jpg")
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

    @Test
    void 클럽_멤버_권한_수정에_성공한다() {
        // given
        given(clubRepository.findById(club1().getId())).willReturn(Optional.of(club1()));
        given(clubUserRepository.findByClub_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(Optional.ofNullable(club1User1Manager()));
        given(clubUserRepository.save(any(ClubUser.class))).willReturn(club1User1Manager().updateRole(ClubUserRole.MEMBER));

        // when, then
        assertThatNoException()
                .isThrownBy(() -> clubService.updateMemberRole(clubUserUpdate));
    }

    @Test
    void 존재하지_않는_클럽의_경우_클럽_멤버_권한_수정에_실패한다() {
        // given
        given(clubRepository.findById(club1().getId())).willThrow(IllegalArgumentException.class);

        // when, then
        assertThatThrownBy(() -> clubService.updateMemberRole(clubUserUpdate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 클럽의_멤버가_아닐_경우_클럽_멤버_권한_수정에_실패한다() {
        // given
        given(clubRepository.findById(club1().getId())).willReturn(Optional.of(club1()));
        given(clubUserRepository.findByClub_IdAndUser_Id(any(Long.class), any(Long.class))).willThrow(IllegalArgumentException.class);

        // when, then
        assertThatThrownBy(() -> clubService.updateMemberRole(clubUserUpdate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 클럽_멤버_탈퇴에_성공한다() {
        // given
        given(clubRepository.findById(club1().getId())).willReturn(Optional.of(club1()));
        given(clubUserRepository.findByClub_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(Optional.ofNullable(club1User1Manager()));
        given(clubUserRepository.countByClub_IdAndRole(any(Long.class), any(ClubUserRole.class))).willReturn(2);

        // when, then
        assertThatNoException()
                .isThrownBy(() -> clubService.deleteMember(club1().getId(), user1().getId()));
    }

    @Test
    void 마지막_관리자인_경우_클럽_멤버_탈퇴에_실패한다() {
        // given
        given(clubRepository.findById(club1().getId())).willReturn(Optional.of(club1()));
        given(clubUserRepository.findByClub_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(Optional.ofNullable(club1User1Manager()));
        given(clubUserRepository.countByClub_IdAndRole(any(Long.class), any(ClubUserRole.class))).willReturn(1);

        // when, then
        assertThatThrownBy(() -> clubService.deleteMember(club1().getId(), user1().getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
