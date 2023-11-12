package com.spaceclub.event.repository;

import com.spaceclub.event.domain.Category;
import com.spaceclub.event.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByClub_Id(Long clubId, Pageable pageable);

    List<Event> findAllByClub_IdAndCategory(Long clubId, Category category);

}
