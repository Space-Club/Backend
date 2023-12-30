package com.spaceclub.user.repository;

import com.spaceclub.user.domain.Email;
import com.spaceclub.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.email = :email and u.oauthUserName = :oauthUsername")
    Optional<User> findByEmailAndOauthUserName(
            @Param("email") Email email,
            @Param("oauthUsername") String oauthUsername
    );

    @Query("""
    select u from User u
    where u.status = 'INACTIVE'
    and u.deletedAt != null
    and u.deletedAt <= :threeDaysAgoFromNow
""")
    List<User> findAllUserToDelete(@Param("threeDaysAgoFromNow") LocalDateTime threeDaysAgoFromNow);

    @Query("select u.email.email from User u where u.id = :userId and u.email.emailConsent = true")
    Optional<String> findEmail(Long userId);

}
