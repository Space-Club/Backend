package com.spaceclub.club.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.global.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.spaceclub.club.domain.ClubUserRole.MANAGER;
import static com.spaceclub.global.ExceptionCode.CLUB_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.NOT_CLUB_MEMBER;
import static com.spaceclub.global.ExceptionCode.UNAUTHORIZED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;

    private final ClubUserRepository clubUserRepository;

    private final S3ImageUploader imageUploader;

    @Transactional
    public Club createClub(Club club, Long userId, MultipartFile logoImage) {

        if (logoImage != null) {
            String logoImageName = imageUploader.uploadClubLogoImage(logoImage);
            club = club.updateLogoImage(logoImageName);
        }

        ClubUser clubUser = ClubUser.builder()
                .club(club)
                .userId(userId)
                .role(MANAGER)
                .build();

        clubUserRepository.save(clubUser);
        return clubRepository.save(club);
    }

    public Club getClub(Long clubId, Long userId) {
        ClubUser clubUser = clubUserRepository.findByClub_IdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_CLUB_MEMBER.toString()));

        return clubUser.getClub();
    }

    @Transactional
    public void deleteClub(Long clubId, Long userId) {
        ClubUser clubUser = clubUserRepository.findByClub_IdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_CLUB_MEMBER.toString()));

        if (clubUser.isNotManager()) throw new IllegalStateException(UNAUTHORIZED.toString());

        clubRepository.deleteById(clubId);
    }

    @Transactional
    public void updateClub(Club newClub, Long userId, MultipartFile logoImage, MultipartFile coverImage) {
        Long clubId = newClub.getId();

        ClubUser clubUser = clubUserRepository.findByClub_IdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_CLUB_MEMBER.toString()));

        if (clubUser.isNotManager()) throw new IllegalArgumentException(UNAUTHORIZED.toString());

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException(CLUB_NOT_FOUND.toString()));

        if (logoImage != null) {
            String logoImageName = imageUploader.uploadClubLogoImage(logoImage);
            club = club.updateLogoImage(logoImageName);
        }

        if (coverImage != null) {
            String coverImageName = imageUploader.uploadClubCoverImage(coverImage);
            club = club.updateCoverImage(coverImageName);
        }

        Club updatedClub = club.update(newClub);

        clubRepository.save(updatedClub);
    }

}
