package com.spaceclub.user.repository;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.user.UserTestFixture;
import com.spaceclub.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;

import static com.spaceclub.user.domain.Status.INACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@DirtiesContext
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class UserRepositoryTest {

    private final LocalDateTime now = LocalDateTime.of(2023, 12, 4, 0, 0, 0);

    @Autowired
    private UserRepository userRepository;

    private User userToDelete1;

    private User userToDelete2;

    @BeforeEach
    void setUp() {
        User user1 = UserTestFixture.user1();
        User user2 = UserTestFixture.user2();
        User user3 = UserTestFixture.deletedUser(4L, INACTIVE, LocalDateTime.of(2023, 12, 1, 1, 0, 0));

        userToDelete1 = UserTestFixture.deletedUser(5L, INACTIVE, LocalDateTime.of(2023, 12, 1, 0, 0, 0));
        userToDelete2 = UserTestFixture.deletedUser(6L, INACTIVE, LocalDateTime.of(2023, 11, 30, 23, 59, 59));

        userRepository.saveAll(List.of(user1, user2, user3, userToDelete1, userToDelete2));
    }

    @Test
    void 삭제할_모든_유저_조회에_성공한다() {
        // given
        final int graceDaysOfDeletion = 3;
        LocalDateTime threeDaysAgoFromNow = now.minusDays(graceDaysOfDeletion);

        // when
        List<User> usersToDelete = userRepository.findAllUserToDelete(threeDaysAgoFromNow);

        // then
        assertAll(
                () -> assertThat(usersToDelete).containsExactlyInAnyOrder(userToDelete1, userToDelete2),
                () -> assertThat(usersToDelete.size()).isEqualTo(2)
        );
    }

}
