package com.spaceclub.form.controller.dto;

import com.spaceclub.event.domain.ApplicationStatus;

public record FormApplicationStatusUpdateRequest(Long formUserId, ApplicationStatus status) {

}
