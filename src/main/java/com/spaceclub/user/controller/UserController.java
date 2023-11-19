package com.spaceclub.user.controller;

import com.spaceclub.club.domain.Club;
import com.spaceclub.event.controller.dto.BookmarkedEventRequest;
import com.spaceclub.event.domain.Event;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.service.JwtManager;
import com.spaceclub.user.controller.dto.UserBookmarkedEventGetResponse;
import com.spaceclub.user.controller.dto.UserClubGetResponse;
import com.spaceclub.user.controller.dto.UserCodeRequest;
import com.spaceclub.user.controller.dto.UserEventGetResponse;
import com.spaceclub.user.controller.dto.UserLoginResponse;
import com.spaceclub.user.controller.dto.UserProfileImageResponse;
import com.spaceclub.user.controller.dto.UserProfileResponse;
import com.spaceclub.user.controller.dto.UserProfileUpdateRequest;
import com.spaceclub.user.controller.dto.UserRequiredInfoRequest;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.service.UserService;
import com.spaceclub.user.service.vo.UserBookmarkInfo;
import com.spaceclub.user.service.vo.UserRequiredInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceclub.user.controller.dto.UserEventGetResponse.from;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtManager jwtManager;

    @GetMapping("/events")
    public PageResponse<UserEventGetResponse, Event> getAllEvents(Pageable pageable, HttpServletRequest request) {
        Long userId = jwtManager.verifyUserId(request);
        Page<Event> eventPages = userService.findAllEventPages(userId, pageable);

        List<UserEventGetResponse> eventGetResponse = eventPages.getContent().stream()
                .map(event -> from(event, userService.findEventStatus(userId, event)))
                .toList();

        return new PageResponse<>(eventGetResponse, eventPages);
    }

    @PostMapping
    public ResponseEntity<UserLoginResponse> createAccount(@RequestBody UserRequiredInfoRequest request) {
        User user = userService.updateRequiredInfo(request.userId(), new UserRequiredInfo(request.name(), request.phoneNumber()));

        String accessToken = jwtManager.createAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtManager.createRefreshToken(user.getId());

        return ResponseEntity.created(URI.create("/api/v1/users/" + user.getId()))
                .body(UserLoginResponse.from(user.getId(), accessToken, refreshToken));
    }

    @PostMapping("/oauths")
    public UserLoginResponse loginUser(@RequestBody UserCodeRequest userCodeRequest) {
        User kakaoUser = userService.createKakaoUser(userCodeRequest.code());

        if (kakaoUser.isNewMember()) {
            return UserLoginResponse.from(kakaoUser.getId(), "", "");
        }

        String accessToken = jwtManager.createAccessToken(kakaoUser.getId(), kakaoUser.getUsername());
        String refreshToken = jwtManager.createRefreshToken(kakaoUser.getId());

        return UserLoginResponse.from(kakaoUser.getId(), accessToken, refreshToken);
    }


    @GetMapping("/profiles")
    public UserProfileResponse getUserProfile(HttpServletRequest request) {
        Long userId = jwtManager.verifyUserId(request);

        return userService.getUserProfile(userId).toResponse();
    }

    @PatchMapping("/required-infos")
    public ResponseEntity<Void> updateUserProfile(@RequestBody UserProfileUpdateRequest request, HttpServletRequest servletRequest) {
        Long userId = jwtManager.verifyUserId(servletRequest);
        userService.updateRequiredInfo(userId, new UserRequiredInfo(request.name(), request.phoneNumber()));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/images")
    public UserProfileImageResponse getUserImage(HttpServletRequest request) {
        Long userId = jwtManager.verifyUserId(request);

        return new UserProfileImageResponse(userService.getUserProfileImage(userId));
    }

    @GetMapping("/clubs")
    public ResponseEntity<List<UserClubGetResponse>> getClubs(HttpServletRequest servletRequest) {
        Long userId = jwtManager.verifyUserId(servletRequest);
        List<Club> clubs = userService.getClubs(userId);

        List<UserClubGetResponse> clubResponses = clubs.stream()
                .map(UserClubGetResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(clubResponses);
    }

    @GetMapping("/bookmarked-events")
    public PageResponse<UserBookmarkedEventGetResponse, Event> getAllBookmarkedEvents(
            Pageable pageable,
            HttpServletRequest servletRequest
    ) {
        Long userId = jwtManager.verifyUserId(servletRequest);
        Page<Event> eventPages = userService.findAllBookmarkedEventPages(userId, pageable);

        List<UserBookmarkedEventGetResponse> bookmarkedEvents = eventPages.getContent().stream()
                .map(UserBookmarkedEventGetResponse::from)
                .toList();

        return new PageResponse<>(bookmarkedEvents, eventPages);
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<Void> bookmarkEvent(@PathVariable Long eventId,
                                              @RequestBody BookmarkedEventRequest request,
                                              HttpServletRequest servletRequest
    ){
        Long userId = jwtManager.verifyUserId(servletRequest);
        userService.changeBookmarkStatus(UserBookmarkInfo.of(eventId, userId, request.bookmark()));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest servletRequest) {
        Long userId = jwtManager.verifyUserId(servletRequest);
        userService.logout(userId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(HttpServletRequest servletRequest) {
        Long userId = jwtManager.verifyUserId(servletRequest);
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> changeUserProfileImage(
            @RequestPart MultipartFile userImage,
            HttpServletRequest servletRequest
    ) {
        Long userId = jwtManager.verifyUserId(servletRequest);
        userService.changeUserProfileImage(userImage, userId);

        return ResponseEntity.noContent().build();
    }

}
