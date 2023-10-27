package com.spaceclub.club.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository repository;

    public Club createClub(Club club) {
        return repository.save(club);
    }

    public Club getClub(Long clubId) {
        return repository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다"));
    }

    public void deleteClub(Long clubId) {
        repository.deleteById(clubId);
    }

}
