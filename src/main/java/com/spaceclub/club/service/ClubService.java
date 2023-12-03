package com.spaceclub.club.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.club.service.vo.ClubInfo;
import com.spaceclub.club.util.ClubValidator;
import com.spaceclub.event.service.EventProvider;
import com.spaceclub.global.config.s3.S3Properties;
import com.spaceclub.global.s3.S3Folder;
import com.spaceclub.global.s3.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.spaceclub.club.ClubExceptionMessage.CLUB_NOT_FOUND;
import static com.spaceclub.club.ClubExceptionMessage.DUPLICATED_CLUB_NAME;
import static com.spaceclub.club.ClubExceptionMessage.NOT_CLUB_MEMBER;
import static com.spaceclub.club.ClubExceptionMessage.UNAUTHORIZED;
import static com.spaceclub.club.domain.ClubUserRole.MANAGER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubService implements ClubProvider {

    private final ClubRepository clubRepository;

    private final ClubUserRepository clubUserRepository;

    private final S3ImageUploader imageUploader;
    
    private final EventProvider eventProvider;

    private final S3Properties s3Properties;

    @Transactional
    public Club createClub(Club club, Long userId, MultipartFile logoImage) {
        ClubValidator.validateClub(club);

        if (logoImage != null) {
            String logoImageName = imageUploader.upload(logoImage, S3Folder.LOGO);
            club = club.updateLogoImage(logoImageName);
        }

        ClubUser clubUser = ClubUser.builder()
                .club(club)
                .userId(userId)
                .role(MANAGER)
                .build();
        try {
            clubUserRepository.save(clubUser);
            return clubRepository.save(club);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(DUPLICATED_CLUB_NAME.toString());
        }
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

        eventProvider.deleteClubEvents(clubId);
        clubRepository.deleteById(clubId);
    }

    @Transactional
    public void updateClub(Club newClub, Long userId, MultipartFile logoImage, MultipartFile coverImage) {
        ClubValidator.validateClub(newClub);

        Long clubId = newClub.getId();

        ClubUser clubUser = clubUserRepository.findByClub_IdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_CLUB_MEMBER.toString()));

        if (clubUser.isNotManager()) throw new IllegalArgumentException(UNAUTHORIZED.toString());

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException(CLUB_NOT_FOUND.toString()));

        if (logoImage != null) {
            String logoImageName = imageUploader.upload(logoImage, S3Folder.LOGO);
            club = club.updateLogoImage(logoImageName);
        }

        if (coverImage != null) {
            String coverImageName = imageUploader.upload(coverImage, S3Folder.COVER);
            club = club.updateCoverImage(coverImageName);
        }

        Club updatedClub = club.update(newClub);

        try {
            clubRepository.save(updatedClub);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(DUPLICATED_CLUB_NAME.toString());
        }

    }

    @Override
    public List<ClubInfo> getClubs(Long userId) {
        return clubUserRepository.findByUserId(userId).stream()
                .map(ClubUser::getClub)
                .map(club -> ClubInfo.from(club, s3Properties.url()))
                .toList();
    }

    @Override
    public Club getClub(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException(CLUB_NOT_FOUND.toString()));
    }

    @Override
    public int getNumberOfClubsUserBelong(Long userId) {
        return clubUserRepository.countByUserId(userId);
    }

}
