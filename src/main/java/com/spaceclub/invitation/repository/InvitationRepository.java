package com.spaceclub.invitation.repository;

import com.spaceclub.invitation.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findByClubId(Long clubId);

}
