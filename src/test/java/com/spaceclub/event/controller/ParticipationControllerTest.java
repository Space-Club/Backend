package com.spaceclub.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.event.controller.dto.EventParticipationCreateRequest;
import com.spaceclub.event.service.ParticipationService;
import com.spaceclub.event.service.vo.EventParticipationCreateInfo;
import com.spaceclub.form.FormTestFixture;
import com.spaceclub.global.UserArgumentResolver;
import com.spaceclub.global.config.WebConfig;
import com.spaceclub.global.interceptor.AuthenticationInterceptor;
import com.spaceclub.global.interceptor.AuthorizationInterceptor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.spaceclub.event.domain.ParticipationStatus.CANCELED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ParticipationController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        WebConfig.class,
                        AuthorizationInterceptor.class,
                        AuthenticationInterceptor.class
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class ParticipationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ParticipationService participationService;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    void 행사_참여_신청_취소에_성공한다() throws Exception {
        // given
        given(participationService.cancel(any(Long.class), any())).willReturn(CANCELED);

        // when
        ResultActions actions = mvc.perform(delete("/api/v1/events/{eventId}/participate", 1L)
                .header(AUTHORIZATION, "Access Token")
                .with(csrf())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("event/cancelParticipate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("eventId").description("행사 id")
                        ),
                        responseFields(
                                fieldWithPath("participationStatus").type(STRING).description("행사 신청 상태(ex. CANCELED, CANCEL_REQUESTED)")
                        )
                ));
    }


    @Test
    @WithMockUser
    void 행사_신청에_성공한다() throws Exception {
        // given
        EventParticipationCreateRequest request = EventParticipationCreateRequest.builder()
                .eventId(1L)
                .ticketCount(5)
                .forms(List.of(
                        new EventParticipationCreateRequest.FormRequest(FormTestFixture.formOption1().getId(), "박씨"),
                        new EventParticipationCreateRequest.FormRequest(FormTestFixture.formOption2().getId(), "010-1111-2222")
                ))
                .build();

        Long userId = 1L;
        doNothing().when(participationService).apply(EventParticipationCreateInfo.builder()
                .userId(userId)
                .eventId(request.eventId())
                .formAnswers(request.toEntityList())
                .ticketCount(request.ticketCount())
                .build()
        );

        // when, then
        mvc.perform(post("/api/v1/events/participate")
                        .header("Authorization", "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andDo(
                        document("event/participate",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                requestFields(
                                        fieldWithPath("eventId").type(NUMBER).description("행사 id"),
                                        fieldWithPath("ticketCount").type(NUMBER).description("행사 예매 매수"),
                                        fieldWithPath("forms[]").type(ARRAY).description("폼 리스트"),
                                        fieldWithPath("forms[].optionId").type(NUMBER).description("폼 항목 id"),
                                        fieldWithPath("forms[].content").type(STRING).description("폼 항목 답변 내용")
                                )
                        )
                );
    }

}
