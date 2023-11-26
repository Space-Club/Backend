package com.spaceclub.user.service;

import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountDeleteScheduler {

    private static final int GRACE_DAYS_OF_DELETION = 3;

    private final UserRepository userRepository;

    @Async
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void deleteInactiveUsers() {
        LocalDateTime threeDaysAgoFromNow = LocalDateTime.now().minusDays(GRACE_DAYS_OF_DELETION);
        List<User> usersToDelete = userRepository.findAllUserToDelete(threeDaysAgoFromNow).stream()
                .map(User::changeStatusToDeleted)
                .toList();

        userRepository.saveAll(usersToDelete);
    }

}
