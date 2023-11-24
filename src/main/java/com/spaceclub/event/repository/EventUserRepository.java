package com.spaceclub.event.repository;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventUserRepository extends JpaRepository<EventUser, Long> {

    @Query("select eu.event from EventUser eu where eu.userId = :userId")
    Page<Event> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select eu.status from EventUser eu where eu.userId = :userId and eu.event = :event")
    String findEventStatusByUserId(@Param("userId") Long userId, @Param("event") Event event);

    List<EventUser> findAllByUserIdAndEvent_IdIn(Long userId, List<Long> eventId);

    @Query("select eu from EventUser eu where eu.event.id = :eventId and eu.userId = :userId")
    Optional<EventUser> findByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);

    @Query("select exists (select eu from EventUser eu where eu.event.id = :eventId and eu.userId = :userId)")
    boolean existsByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);

    Page<EventUser> findByEvent(Event event, Pageable pageable);

    Integer countByEvent_Id(Long eventId);

}
