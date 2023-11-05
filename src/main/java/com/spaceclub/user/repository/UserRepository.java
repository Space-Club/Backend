package com.spaceclub.user.repository;

import com.spaceclub.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.email = :email and u.oauthUserName = :oauthUsername")
    Optional<User> findByEmailAndOauthUserName(
            @Param("email") String email,
            @Param("oauthUsername") String oauthUsername
    );

}
