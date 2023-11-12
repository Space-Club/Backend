package com.spaceclub.form.controller.dto;

import com.spaceclub.event.domain.ApplicationStatus;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormOptionUser;
import com.spaceclub.form.service.vo.FormApplicationGetInfo;
import lombok.Builder;

import java.util.List;

public record FormApplicationGetResponse(
        FormInfoResponse formInfo,
        List<UserFormResponse> userForms
) {

    @Builder
    public FormApplicationGetResponse {

    }

    public static FormApplicationGetResponse fromVo(FormApplicationGetInfo vo) {
        Form form = vo.form();
        List<EventUser> eventUsers = vo.eventUsers();
        List<FormOptionUser> formOptionUsers = vo.formOptionUsers();

        List<String> optionTitles = form.getOptions().stream()
                .map(FormOption::getTitle)
                .toList();

        FormInfoResponse formInfoResponse = new FormInfoResponse(eventUsers.size(), optionTitles, form.isManaged());
        List<UserFormResponse> userForms = generateUserFormResponses(eventUsers, formOptionUsers);

        return FormApplicationGetResponse.builder()
                .formInfo(formInfoResponse)
                .userForms(userForms)
                .build();
    }

    private static List<UserFormResponse> generateUserFormResponses(List<EventUser> eventUsers, List<FormOptionUser> formOptionUsers) {
        return eventUsers.stream()
                .map(eventUser -> {
                    Long userId = eventUser.getUser().getId();

                    List<UserFormOptionResponse> options = formOptionUsers.stream()
                            .filter(formOptionUser -> formOptionUser.getUserId().equals(userId))
                            .map(formOptionUser -> new UserFormOptionResponse(formOptionUser.getOptionTitle(), formOptionUser.getContent()))
                            .toList();

                    return new UserFormResponse(eventUser.getId(), options, eventUser.getStatus());
                })
                .toList();
    }

    public record FormInfoResponse(int count, List<String> optionTitles, boolean managed) {

    }

    private record UserFormResponse(
            Long id,
            List<UserFormOptionResponse> options,
            ApplicationStatus applicationStatus
    ) {

    }

    public record UserFormOptionResponse(String title, String content) {

    }

}
