package com.spaceclub.form.controller.dto;

import com.spaceclub.event.domain.ParticipationStatus;

public record FormSubmitUpdateRequest(Long formUserId, ParticipationStatus participationStatus) {

}
