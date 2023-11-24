package com.spaceclub.user.repository;

import com.spaceclub.user.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

        Optional<Bookmark> findByUserIdAndEventId(Long userId, Long eventId);

        Boolean existsByUserIdAndEventId(Long userId, Long eventId);

}
