package com.spaceclub.user.service;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.club.ClubUserTestFixture.club1User1Manager;
import static com.spaceclub.user.UserTestFixture.user1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class UserServiceTest {

    @Mock
    private ClubUserRepository clubUserRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void 모든_클럽_조회에_성공한다() {
        // given
        given(userRepository.existsById(any(Long.class))).willReturn(true);
        given(clubUserRepository.findByUser_Id(user1().getId())).willReturn(List.of(club1User1Manager()));

        // when
        List<Club> clubs = userService.getClubs(user1().getId());

        // then
        assertThat(clubs).usingRecursiveComparison().isEqualTo(List.of(club1()));
    }


}
