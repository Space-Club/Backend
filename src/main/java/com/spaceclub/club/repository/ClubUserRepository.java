package com.spaceclub.club.repository;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubUserRepository extends JpaRepository<ClubUser, Long> {

    List<ClubUser> findByClub_Id(Long clubId);

    Optional<ClubUser> findByClub_IdAndUserId(Long clubId, Long userId);

    int countByClub_IdAndRole(Long clubId, ClubUserRole role);

    List<ClubUser> findByUserId(Long userId);

    boolean existsByClubAndUserId(Club club, Long userId);

    Long countByClub(Club club);

    ClubUser findByClub_IdAndRole(Long clubId, ClubUserRole role);

    Boolean existsByClub_IdAndUserId(Long clubId, Long userId);

    int countByUserId(Long userId);

}
