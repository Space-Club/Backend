package com.spaceclub.user.service;

import com.spaceclub.user.domain.Bookmark;
import com.spaceclub.user.repository.BookmarkRepository;
import com.spaceclub.user.service.vo.UserBookmarkInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spaceclub.global.ExceptionCode.ALREADY_BOOKMARKED;
import static com.spaceclub.global.ExceptionCode.BAD_REQUEST;
import static com.spaceclub.global.ExceptionCode.BOOKMARK_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkCommandProvider bookmarkCommand;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public void changeBookmarkStatus(UserBookmarkInfo userBookmarkInfo) {
        switch (bookmarkCommand.getCommand(userBookmarkInfo)) {
            case REGISTER -> register(userBookmarkInfo);
            case CANCEL -> cancel(userBookmarkInfo);
            case ALREADY_BOOKMARKED -> throw new IllegalArgumentException(ALREADY_BOOKMARKED.toString());
            case BAD_REQUEST -> throw new IllegalArgumentException(BAD_REQUEST.toString());
        }
    }

    private void register(UserBookmarkInfo userBookmarkInfo) {
        Bookmark bookmark = bookmarkRepository.save(Bookmark.builder()
                .userId(userBookmarkInfo.userId())
                .eventId(userBookmarkInfo.eventId())
                .build());
        bookmarkRepository.save(bookmark);
    }

    private void cancel(UserBookmarkInfo userBookmarkInfo) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndEventId(userBookmarkInfo.userId(), userBookmarkInfo.eventId())
                .orElseThrow(() -> new IllegalArgumentException(BOOKMARK_NOT_FOUND.toString()));

        bookmarkRepository.delete(bookmark);
    }

}
