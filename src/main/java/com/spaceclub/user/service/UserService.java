package com.spaceclub.user.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.global.S3ImageUploader;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.spaceclub.global.ExceptionCode.ALREADY_BOOKMARKED;
import static com.spaceclub.global.ExceptionCode.BAD_REQUEST;
import static com.spaceclub.global.ExceptionCode.BOOKMARK_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.EVENT_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final KakaoOauthInfoSender kakaoOauthInfoSender;

    private final EventUserRepository eventUserRepository;

    private final ClubUserRepository clubUserRepository;

    private final UserRepository userRepository;

    private final BookmarkRepository bookmarkRepository;

    private final S3ImageUploader imageUploader;

    private final EventRepository eventRepository;

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
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND.toString()))
                .updateRequiredInfo(userRequiredInfo.name(), userRequiredInfo.phoneNumber());

        return userRepository.save(user);
    }

    public UserProfileInfo getUserProfile(Long userId) {
        User user = getUser(userId);

        return new UserProfileInfo(user.getUsername(), user.getPhoneNumber(), user.getProfileImageUrl());
    }

    public String getUserProfileImage(Long userId) {
        User user = getUser(userId);

        return user.getProfileImageUrl();
    }

    public List<Club> getClubs(Long userId) {
        if (!userRepository.existsById(userId)) throw new IllegalStateException(USER_NOT_FOUND.toString());
        List<ClubUser> clubUsers = clubUserRepository.findByUser_Id(userId);

        return clubUsers.stream()
                .map(ClubUser::getClub)
                .collect(Collectors.toList());
    }

    public Page<Event> findAllBookmarkedEventPages(Long userId, Pageable pageable) {
        User user = getUser(userId);

        return bookmarkRepository.findBookmarkedEventPages(user, pageable);
    }

    @Transactional
    public void changeBookmarkStatus(UserBookmarkInfo userBookmarkInfo) {
        Event event = eventRepository.findById(userBookmarkInfo.eventId())
                .orElseThrow(() -> new IllegalArgumentException(EVENT_NOT_FOUND.toString()));
        User user = getUser(userBookmarkInfo.userId());

        if (userBookmarkInfo.bookmarkStatus() && !bookmarkExists(userBookmarkInfo)) {
            Bookmark bookmark = Bookmark.builder()
                    .user(user)
                    .event(event)
                    .build();
            bookmarkRepository.save(bookmark);
            return;
        }
        if (!userBookmarkInfo.bookmarkStatus() && bookmarkExists(userBookmarkInfo)) {
            Bookmark bookmark = bookmarkRepository.findByUserIdAndEventId(userBookmarkInfo.userId(), userBookmarkInfo.eventId())
                    .orElseThrow(() -> new IllegalArgumentException(BOOKMARK_NOT_FOUND.toString()));

            bookmarkRepository.delete(bookmark);
            return;
        }
        if (userBookmarkInfo.bookmarkStatus() && bookmarkExists(userBookmarkInfo)) {
            throw new IllegalArgumentException(ALREADY_BOOKMARKED.toString());
        }
        if (!userBookmarkInfo.bookmarkStatus() && !bookmarkExists(userBookmarkInfo)) {
            throw new IllegalArgumentException(BAD_REQUEST.toString());
        }
    }

    private boolean bookmarkExists(UserBookmarkInfo userBookmarkInfo) {
        return bookmarkRepository
                .findByUserIdAndEventId(userBookmarkInfo.userId(), userBookmarkInfo.eventId())
                .isPresent();
    }

    public void logout(Long userId) {
        User user = getUser(userId);

        kakaoOauthInfoSender.logout(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = getUser(userId);

        userRepository.delete(user); // 추후 정책 반영에 따라 수정 예정

        kakaoOauthInfoSender.unlink(user);
    }

    @Transactional
    public void changeUserProfileImage(MultipartFile userImage, Long userId) {
        String profileUrl = imageUploader.uploadUserProfileImage(userImage);
        User user = getUser(userId);
        userRepository.save(user.changeProfileImageUrl(profileUrl));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND.toString()));
    }

}
