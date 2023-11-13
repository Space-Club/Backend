package com.spaceclub.user.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.global.oauth.config.KakaoOauthInfoSender;
import com.spaceclub.global.oauth.config.vo.KakaoTokenInfo;
import com.spaceclub.global.oauth.config.vo.KakaoUserInfo;
import com.spaceclub.user.domain.Bookmark;
import com.spaceclub.user.domain.Email;
import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.BookmarkRepository;
import com.spaceclub.user.repository.UserRepository;
import com.spaceclub.user.service.vo.UserBookmarkInfo;
import com.spaceclub.user.service.vo.UserProfileInfo;
import com.spaceclub.user.service.vo.UserRequiredInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final KakaoOauthInfoSender kakaoOauthInfoSender;

    private final EventUserRepository eventUserRepository;

    private final ClubUserRepository clubUserRepository;

    private final UserRepository userRepository;

    private final BookmarkRepository bookmarkRepository;

    public Page<Event> findAllEventPages(Long userId, Pageable pageable) {
        return eventUserRepository.findAllByUserId(userId, pageable);
    }

    public String findEventStatus(Long userId, Event event) {
        return eventUserRepository.findEventStatusByUserId(userId, event);
    }

    @Transactional
    public User createKakaoUser(String code) {
        KakaoTokenInfo accessTokenInfo = kakaoOauthInfoSender.getAccessTokenInfo(code);
        String accessToken = accessTokenInfo.accessToken();
        KakaoUserInfo userInfo = kakaoOauthInfoSender.getUserInfo(accessToken);

        return userRepository.findByEmailAndOauthUserName(new Email(userInfo.email()), Provider.KAKAO.name() + userInfo.id())
                .orElseGet(() -> userRepository.save(userInfo.toUser()));
    }

    @Transactional
    public User updateRequiredInfo(Long userId, UserRequiredInfo userRequiredInfo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."))
                .updateRequiredInfo(userRequiredInfo.name(), userRequiredInfo.phoneNumber());

        return userRepository.save(user);
    }

    public UserProfileInfo getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        return new UserProfileInfo(user.getUsername(), user.getPhoneNumber(), user.getProfileImageUrl());
    }

    public String getUserProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        return user.getProfileImageUrl();
    }

    public List<Club> getClubs(Long userId) {
        List<ClubUser> clubUsers = clubUserRepository.findByUser_Id(userId);

        return clubUsers.stream()
                .map(ClubUser::getClub)
                .collect(Collectors.toList());
    }

    public Page<Event> findAllBookmarkedEventPages(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        return bookmarkRepository.findBookmarkedEventPages(user, pageable);
    }

    @Transactional
    public void changeBookmarkStatus(UserBookmarkInfo userBookmarkInfo) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndEventId(userBookmarkInfo.userId(), userBookmarkInfo.eventId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 북마크입니다."))
                .changeBookmarkStatus(userBookmarkInfo.bookmarkStatus());

        bookmarkRepository.save(bookmark);
    }

    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        kakaoOauthInfoSender.logout(user);
    }

}
