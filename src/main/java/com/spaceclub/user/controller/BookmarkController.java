package com.spaceclub.user.controller;

import com.spaceclub.event.controller.dto.BookmarkedEventRequest;
import com.spaceclub.event.service.EventProvider;
import com.spaceclub.event.service.vo.UserBookmarkedEventGetInfo;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.vo.JwtUser;
import com.spaceclub.user.controller.dto.UserBookmarkedEventGetResponse;
import com.spaceclub.user.service.BookmarkService;
import com.spaceclub.user.service.vo.UserBookmarkInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final EventProvider eventProvider;

    @GetMapping("/events/bookmark")
    public PageResponse<UserBookmarkedEventGetResponse, UserBookmarkedEventGetInfo> getAllBookmarkedEvents(
            Pageable pageable,
            @Authenticated JwtUser jwtUser
    ) {
        Page<UserBookmarkedEventGetInfo> eventPages = eventProvider.findAllBookmarkedEventPages(jwtUser.id(), pageable);

        List<UserBookmarkedEventGetResponse> bookmarkedEvents = eventPages.getContent().stream()
                .map(UserBookmarkedEventGetResponse::from)
                .toList();

        return new PageResponse<>(bookmarkedEvents, eventPages);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public void bookmarkEvent(
            @PathVariable Long eventId,
            @RequestBody BookmarkedEventRequest request,
            @Authenticated JwtUser jwtUser
    ) {
        UserBookmarkInfo bookmarkInfo = UserBookmarkInfo.of(eventId, jwtUser.id(), request.bookmark());

        bookmarkService.changeBookmarkStatus(bookmarkInfo);
    }

    @GetMapping("/events/{eventId}")
    public boolean isBookmarked(@PathVariable Long eventId, @Authenticated JwtUser jwtUser) {
        Long userId = jwtUser.id();

        return bookmarkService.isBookmarked(userId, eventId);
    }

}
