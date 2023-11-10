package com.spaceclub.invite.service;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.invite.repository.InviteRepository;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.invite.InviteTestFixture.invite1;
import static com.spaceclub.user.UserTestFixture.user1;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class InviteServiceTest {

    @InjectMocks
    private InviteService inviteService;

    @Mock
    private InviteRepository inviteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClubUserRepository clubUserRepository;

    @Test
    void 클럽_초대_링크가_만료된_경우_클럽_가입에_실패한다() {
        // given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user1()));
        given(inviteRepository.findByCode(any(String.class))).willReturn(Optional.of(invite1()));

        // when, then
        assertThatThrownBy(() -> inviteService.joinClub("123", 1L))
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    void 해당_클럽에_이미_가입한_경우_클럽_가입에_실패한다() {
        // given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user1()));
        given(inviteRepository.findByCode(any(String.class))).willReturn(Optional.of(invite1()));
        lenient().doReturn(true).when(clubUserRepository)
                .existsByClubAndUser(any(Club.class), any(User.class));

        // when, then
        assertThatThrownBy(() -> inviteService.joinClub("123", 1L))
                .isInstanceOf(IllegalStateException.class);

    }

}
