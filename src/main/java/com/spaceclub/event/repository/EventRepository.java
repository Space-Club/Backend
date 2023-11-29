package com.spaceclub.event.repository;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByClub_Id(Long clubId, Pageable pageable);

    Page<Event> findAllByCategory(EventCategory category, Pageable pageable);

    Page<Event> findByEventInfo_TitleContainsIgnoreCase(String searchWord, Pageable pageable);

    @Query("select e from Event e join fetch Bookmark b on e.id = b.eventId where b.userId = :userId")
    Page<Event> findAllBookmarkedEventPages(@Param("userId") Long userId, Pageable pageable);

    Page<Event> findAllByFormCloseDateTimeGreaterThan(LocalDateTime now, Pageable pageable);

}
