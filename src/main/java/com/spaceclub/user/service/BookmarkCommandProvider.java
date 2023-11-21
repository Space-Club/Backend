package com.spaceclub.user.service;

import com.spaceclub.user.repository.BookmarkRepository;
import com.spaceclub.user.service.vo.BookmarkCommand;
import com.spaceclub.user.service.vo.UserBookmarkInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.spaceclub.user.service.vo.BookmarkCommand.ALREADY_BOOKMARKED;
import static com.spaceclub.user.service.vo.BookmarkCommand.BAD_REQUEST;
import static com.spaceclub.user.service.vo.BookmarkCommand.CANCEL;
import static com.spaceclub.user.service.vo.BookmarkCommand.REGISTER;

@Component
@RequiredArgsConstructor
public class BookmarkCommandProvider {

    private final BookmarkRepository bookmarkRepository;

    public BookmarkCommand getCommand(UserBookmarkInfo userBookmarkInfo) {
        if (userBookmarkInfo.bookmarkStatus() && !bookmarkExists(userBookmarkInfo)) {
            return REGISTER;
        }
        if (!userBookmarkInfo.bookmarkStatus() && bookmarkExists(userBookmarkInfo)) {
            return CANCEL;
        }
        if (userBookmarkInfo.bookmarkStatus() && bookmarkExists(userBookmarkInfo)) {
            return ALREADY_BOOKMARKED;
        }
        return BAD_REQUEST;
    }

    private boolean bookmarkExists(UserBookmarkInfo userBookmarkInfo) {
        return bookmarkRepository
                .findByUserIdAndEventId(userBookmarkInfo.userId(), userBookmarkInfo.eventId())
                .isPresent();
    }

}
