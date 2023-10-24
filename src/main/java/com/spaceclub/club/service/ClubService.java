package com.spaceclub.club.service;

import com.spaceclub.club.repository.ClubRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubService {

    private final ClubRepository repository;

}
