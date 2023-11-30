package com.spaceclub.form.controller.dto;

import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.domain.ParticipationStatus;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormAnswer;
import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.service.vo.FormSubmitGetInfo;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static com.spaceclub.event.domain.EventCategory.SHOW;

public record FormSubmitGetResponse(
        FormInfoResponse formInfo,
        List<UserFormResponse> userForms,
        PageableResponse pageData
) {

    public static FormSubmitGetResponse from(FormSubmitGetInfo vo) {
        Form form = vo.form();
        Page<EventUser> eventUsers = vo.eventUsers();
        List<FormAnswer> formAnswers = vo.formAnswers();

        List<String> optionTitles = form.getOptions().stream()
                .map(FormOption::getTitle)
                .toList();

        FormInfoResponse formInfoResponse = new FormInfoResponse(eventUsers.getTotalElements(), optionTitles, form.isManaged());
        Page<UserFormResponse> userFormPages = generateUserFormResponses(eventUsers, formAnswers);

        return new FormSubmitGetResponse(formInfoResponse, userFormPages.getContent(), PageableResponse.from(userFormPages));
    }

    private static Page<UserFormResponse> generateUserFormResponses(Page<EventUser> eventUsers, List<FormAnswer> formAnswers) {
        List<UserFormResponse> userFormResponses = eventUsers.getContent().stream()
                .map(eventUser -> {
                    Long userId = eventUser.getUserId();

                    List<UserFormOptionResponse> options = formAnswers.stream()
                            .filter(formAnswer -> formAnswer.getUserId().equals(userId))
                            .map(formAnswer -> new UserFormOptionResponse(formAnswer.getOptionTitle(), formAnswer.getContent()))
                            .toList();

                    return UserFormResponse.builder()
                            .userId(userId)
                            .options(options)
                            .participation(new ParticipationResponse(eventUser.getStatus(), eventUser.getParticipationDateTime()))
                            .ticketCount(SHOW.equals(eventUser.getEventCategory()) ? eventUser.getTicketCount() : null)
                            .build();

                })
                .toList();

        return new PageImpl<>(userFormResponses, eventUsers.getPageable(), eventUsers.getTotalElements());
    }


    private record FormInfoResponse(long count, List<String> optionTitles, boolean managed) {

    }

    private record UserFormResponse(
            Long userId,
            List<UserFormOptionResponse> options,
            ParticipationResponse participation,
            Integer ticketCount
    ) {

        @Builder
        private UserFormResponse {

        }

    }

    private record UserFormOptionResponse(String title, String content) {

    }

    private record ParticipationResponse(ParticipationStatus status, String dateTime) {

    }

    private record PageableResponse(boolean first,
                                    boolean last,
                                    int pageNumber,
                                    int size,
                                    int totalPages,
                                    long totalElements) {

        private static PageableResponse from(Page<UserFormResponse> page) {
            return new PageableResponse(
                    page.isFirst(),
                    page.isLast(),
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalPages(),
                    page.getTotalElements()
            );
        }

    }

}
