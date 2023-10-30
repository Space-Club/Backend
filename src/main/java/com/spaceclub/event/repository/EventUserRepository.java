package com.spaceclub.event.repository;

import com.spaceclub.event.domain.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventUserRepository extends JpaRepository<EventUser, Long> {

}
