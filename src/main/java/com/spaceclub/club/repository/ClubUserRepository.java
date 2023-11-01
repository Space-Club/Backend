package com.spaceclub.club.repository;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubUserRepository extends JpaRepository<ClubUser, Long> {

    List<ClubUser> findByClub_Id(Long clubId);
    
    Optional<ClubUser> findByClub_IdAndUser_Id(Long clubId, Long userId);

}