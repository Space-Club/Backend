package com.spaceclub.event.controller.dto;

import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormOptionUser;
import lombok.Builder;

import java.util.List;

public record EventParticipationCreateRequest(Long eventId, Integer ticketCount, List<FormRequest> forms) {

    @Builder
    public EventParticipationCreateRequest {
    }

    public record FormRequest(Long optionId, String content) {

        public FormOptionUser toEntity() {
            return FormOptionUser.builder()
                    .formOption(
                            FormOption.builder()
                                    .id(optionId)
                                    .build()
                    )
                    .content(content)
                    .build();
        }

    }

    public List<FormOptionUser> toEntityList() {
        return forms.stream()
                .map(FormRequest::toEntity)
                .toList();
    }

}
