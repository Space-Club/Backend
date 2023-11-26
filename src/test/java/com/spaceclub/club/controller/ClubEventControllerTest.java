package com.spaceclub.club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.service.ClubEventService;
import com.spaceclub.event.service.vo.EventGetInfo;
import com.spaceclub.global.UserArgumentResolver;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.spaceclub.event.EventTestFixture.clubEvent;
import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.showEvent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = ClubEventController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        AuthorizationInterceptor.class,
                        AuthenticationInterceptor.class
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class ClubEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ClubEventService clubEventService;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    public void 클럽_행사_조회에_성공한다() throws Exception {
        // given
        List<EventGetInfo> events = List.of(EventGetInfo.from(event1()),
                EventGetInfo.from(showEvent()),
                EventGetInfo.from(clubEvent()));
        Page<EventGetInfo> eventPages = new PageImpl<>(events);

        given(clubEventService.getClubEvents(any(Long.class), any(Pageable.class), any())).willReturn(eventPages);
        Long clubId = 1L;

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/clubs/{clubId}/events", clubId)
                .header(AUTHORIZATION, "access token")
                .param("page", "1")
                .param("size", "3")
                .param("sort", "id,asc")
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(events.size()))
                .andExpect(jsonPath("$.pageData.first").value(true))
                .andExpect(jsonPath("$.pageData.last").value(true))
                .andExpect(jsonPath("$.pageData.pageNumber").value(0))
                .andExpect(jsonPath("$.pageData.size").value(3))
                .andExpect(jsonPath("$.pageData.totalPages").value(1))
                .andExpect(jsonPath("$.pageData.totalElements").value(events.size()))
                .andDo(document("club/getAllEvent",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")),
                        queryParameters(
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("페이지 내 개수"),
                                parameterWithName("sort").description("정렬 방법((ex) id,desc)")
                        ),
                        responseFields(
                                fieldWithPath("data").type(ARRAY).description("페이지 내 행사 정보"),
                                fieldWithPath("data[].id").type(NUMBER).description("행사 id"),
                                fieldWithPath("data[].eventInfo").type(OBJECT).description("행사 정보"),
                                fieldWithPath("data[].eventInfo.title").type(STRING).description("행사 제목"),
                                fieldWithPath("data[].eventInfo.posterImageUrl").type(STRING).description("포스터 URL"),
                                fieldWithPath("data[].eventInfo.location").type(STRING).description("행사 위치"),
                                fieldWithPath("data[].eventInfo.startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("data[].eventInfo.startTime").type(STRING).description("행사 시작 시간"),
                                fieldWithPath("data[].eventInfo.endDate").type(STRING).description("행사 종료 날짜"),
                                fieldWithPath("data[].eventInfo.endTime").type(STRING).description("행사 종료 시간"),
                                fieldWithPath("data[].eventInfo.openStatus").type(STRING).description("행사 공개 여부"),
                                fieldWithPath("data[].eventInfo.isEnded").type(BOOLEAN).description("행사 종료 여부"),
                                fieldWithPath("data[].clubInfo").type(OBJECT).description("클럽 정보"),
                                fieldWithPath("data[].clubInfo.name").type(STRING).description("클럽 명"),
                                fieldWithPath("data[].clubInfo.logoImageUrl").type(STRING).description("클럽 이미지 Url"),
                                fieldWithPath("pageData").type(OBJECT).description("페이지 정보"),
                                fieldWithPath("pageData.first").type(BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("pageData.last").type(BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("pageData.pageNumber").type(NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("pageData.size").type(NUMBER).description("페이지 내 개수"),
                                fieldWithPath("pageData.totalPages").type(NUMBER).description("총 페이지 개수"),
                                fieldWithPath("pageData.totalElements").type(NUMBER).description("총 행사 개수")
                        )
                ));
    }

}
