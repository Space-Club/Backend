package com.spaceclub.user.controller;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.club.service.vo.ClubInfo;
import com.spaceclub.event.service.ParticipationProvider;
import com.spaceclub.event.service.vo.EventPageInfo;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.club.ClubTestFixture.club2;
import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.eventUser;
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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ContentController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        AuthorizationInterceptor.class,
                        AuthenticationInterceptor.class
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class ContentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ParticipationProvider participationService;

    @MockBean
    private ClubService clubService;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    void 유저의_모든_이벤트_조회에_성공한다() throws Exception {
        // given
        List<EventPageInfo> pageInfos = List.of(EventPageInfo.from(event1(), eventUser()));
        PageRequest pageRequest = PageRequest.of(0, 10);
        int total = 1;

        PageImpl<EventPageInfo> eventPages = new PageImpl<>(pageInfos, pageRequest, total);
        given(participationService.findAllEventPages(any(), any(Pageable.class))).willReturn(eventPages);

        // when, then
        mvc.perform(get("/api/v1/users/events")
                        .header(AUTHORIZATION, "access token")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "id,asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(1))
                .andExpect(jsonPath("$.pageData.first").value(true))
                .andExpect(jsonPath("$.pageData.last").value(true))
                .andExpect(jsonPath("$.pageData.pageNumber").value(0))
                .andExpect(jsonPath("$.pageData.size").value(10))
                .andExpect(jsonPath("$.pageData.totalPages").value(1))
                .andExpect(jsonPath("$.pageData.totalElements").value(1))
                .andDo(
                        document("user/getAllEvents",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                queryParameters(
                                        parameterWithName("page").description("페이지"),
                                        parameterWithName("size").description("페이지 내 개수"),
                                        parameterWithName("sort").description("정렬 방법((ex) id,asc)")
                                ),
                                responseFields(
                                        fieldWithPath("data").type(ARRAY).description("페이지 내 이벤트 정보"),
                                        fieldWithPath("data[].id").type(NUMBER).description("이벤트 아이디"),
                                        fieldWithPath("data[].title").type(STRING).description("이벤트 제목"),
                                        fieldWithPath("data[].location").type(STRING).description("이벤트 위치"),
                                        fieldWithPath("data[].clubName").type(STRING).description("이벤트 주최자"),
                                        fieldWithPath("data[].posterImageUrl").type(STRING).description("포스터 URL"),
                                        fieldWithPath("data[].startDate").type(STRING).description("이벤트 시작일"),
                                        fieldWithPath("data[].participationStatus").type(STRING).description("이벤트 신청 상태"),
                                        fieldWithPath("pageData").type(OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageData.first").type(BOOLEAN).description("첫 페이지 여부"),
                                        fieldWithPath("pageData.last").type(BOOLEAN).description("마지막 페이지 여부"),
                                        fieldWithPath("pageData.pageNumber").type(NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("pageData.size").type(NUMBER).description("페이지 내 개수"),
                                        fieldWithPath("pageData.totalPages").type(NUMBER).description("총 페이지 개수"),
                                        fieldWithPath("pageData.totalElements").type(NUMBER).description("총 이벤트 개수")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 유저의_모든_클럽_조회에_성공한다() throws Exception {
        // given
        ClubInfo club1 = ClubInfo.from(club1());
        ClubInfo club2 = ClubInfo.from(club2());
        given(clubService.getClubs(any())).willReturn(List.of(club1, club2));

        // when, then
        mvc.perform(get("/api/v1/users/clubs")
                        .header(AUTHORIZATION, "access token"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("user/getAllClubs",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("[]").type(ARRAY).description("클럽"),
                                        fieldWithPath("[].id").type(NUMBER).description("클럽 아이디"),
                                        fieldWithPath("[].logoImageUrl").type(STRING).description("클럽 이미지 Url"),
                                        fieldWithPath("[].name").type(STRING).description("클럽 이름")
                                )
                        )
                );
    }

}
