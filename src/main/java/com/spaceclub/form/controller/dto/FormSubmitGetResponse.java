package com.spaceclub.form.controller.dto;

import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.domain.ParticipationStatus;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormOptionUser;
import com.spaceclub.form.service.vo.FormSubmitGetInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public record FormSubmitGetResponse(
        FormInfoResponse formInfo,
        List<UserFormResponse> userForms,
        PageableResponse pageData
) {

    public static FormSubmitGetResponse from(FormSubmitGetInfo vo) {
        Form form = vo.form();
        Page<EventUser> eventUsers = vo.eventUsers();
        List<FormOptionUser> formOptionUsers = vo.formOptionUsers();

        List<String> optionTitles = form.getOptions().stream()
                .map(FormOption::getTitle)
                .toList();

        FormInfoResponse formInfoResponse = new FormInfoResponse(eventUsers.getTotalElements(), optionTitles, form.isManaged());
        Page<UserFormResponse> userFormPages = generateUserFormResponses(eventUsers, formOptionUsers);

        return new FormSubmitGetResponse(formInfoResponse, userFormPages.getContent(), PageableResponse.from(userFormPages));
    }

    private static Page<UserFormResponse> generateUserFormResponses(Page<EventUser> eventUsers, List<FormOptionUser> formOptionUsers) {
        List<UserFormResponse> userFormResponses = eventUsers.getContent().stream()
                .map(eventUser -> {
                    Long userId = eventUser.getUserId();

                    List<UserFormOptionResponse> options = formOptionUsers.stream()
                            .filter(formOptionUser -> formOptionUser.getUserId().equals(userId))
                            .map(formOptionUser -> new UserFormOptionResponse(formOptionUser.getOptionTitle(), formOptionUser.getContent()))
                            .toList();

                    return new UserFormResponse(userId, options, eventUser.getStatus());
                })
                .toList();

        return new PageImpl<>(userFormResponses, eventUsers.getPageable(), eventUsers.getTotalElements());
    }


    private record FormInfoResponse(long count, List<String> optionTitles, boolean managed) {

    }

    private record UserFormResponse(
            Long userId,
            List<UserFormOptionResponse> options,
            ParticipationStatus participationStatus
    ) {

    }

    private record UserFormOptionResponse(String title, String content) {

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
