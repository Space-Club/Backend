package com.spaceclub.club.repository;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.club.ClubTestFixture.clubUser;
import static com.spaceclub.user.UserTestFixture.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class ClubUserRepositoryTest {

    @Autowired
    private ClubUserRepository clubUserRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 클럽의_유저_조회에_성공한다() {
        // given
        clubRepository.save(club1());
        userRepository.save(user());
        clubUserRepository.save(clubUser());

        // when
        Optional<ClubUser> getClubUser = clubUserRepository.findByClub_IdAndUser_Id(club1().getId(), user().getId());
        Optional<ClubUser> getEmpty = clubUserRepository.findByClub_IdAndUser_Id(club1().getId(), user().getId() + 1);

        // then
        assertAll(
                () -> assertThat(getClubUser).isNotEmpty(),
                () -> assertThat(getEmpty).isEmpty()
        );
    }

}
