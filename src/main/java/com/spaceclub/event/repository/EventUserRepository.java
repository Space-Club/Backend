package com.spaceclub.event.repository;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventUserRepository extends JpaRepository<EventUser, Long> {

    @Query("select eu.event from EventUser eu where eu.user.id = :userId")
    Page<Event> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select eu.status from EventUser eu where eu.user.id = :userId and eu.event = :event")
    String findEventStatusByUserId(@Param("userId") Long userId, @Param("event") Event event);

    EventUser findByEvent_IdAndUser_Id(Long eventId, Long userId);
}
