package com.spaceclub.club.repository;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findByInvitation(Invitation invitation);

}
