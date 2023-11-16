package com.spaceclub.user.repository;

import com.spaceclub.event.domain.Event;
import com.spaceclub.user.domain.Bookmark;
import com.spaceclub.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

        Optional<Bookmark> findByUserIdAndEventId(Long userId, Long eventId);

        @Query("select b.event from Bookmark b where b.user = :user")
        Page<Event> findBookmarkedEventPages(@Param("user") User user, Pageable pageable);

}
