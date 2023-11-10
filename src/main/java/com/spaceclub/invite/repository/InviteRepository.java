package com.spaceclub.invite.repository;

import com.spaceclub.club.domain.Club;
import com.spaceclub.invite.domain.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> {

    Optional<Invite> findByClub(Club club);

    Optional<Invite> findByCode(String code);

}
