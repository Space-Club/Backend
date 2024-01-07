package com.spaceclub.event.repository;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Event> findWithLockById(Long id);

    Page<Event> findByClub_Id(Long clubId, Pageable pageable);

    List<Event> findByClub_Id(Long clubId);

    Page<Event> findAllByCategory(EventCategory category, Pageable pageable);

    Page<Event> findByEventInfo_TitleContainsIgnoreCase(String searchWord, Pageable pageable);

    @Query("select e from Event e join fetch Bookmark b on e.id = b.eventId where b.userId = :userId")
    Page<Event> findAllBookmarkedEventPages(@Param("userId") Long userId, Pageable pageable);

    Page<Event> findAllByCategoryNotAndFormInfo_FormCloseDateTimeGreaterThanAndFormInfo_FormOpenDateTimeLessThan(EventCategory category, LocalDateTime closeDate, LocalDateTime openDate, Pageable pageable);

}
