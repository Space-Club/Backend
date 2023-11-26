package com.spaceclub.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.event.controller.dto.BookmarkedEventRequest;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.service.EventService;
import com.spaceclub.event.service.vo.UserBookmarkedEventGetInfo;
import com.spaceclub.global.UserArgumentResolver;
import com.spaceclub.global.interceptor.AuthenticationInterceptor;
import com.spaceclub.global.interceptor.AuthorizationInterceptor;
import com.spaceclub.user.service.BookmarkService;
import com.spaceclub.user.service.vo.UserBookmarkInfo;
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
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookmarkController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        AuthorizationInterceptor.class,
                        AuthenticationInterceptor.class
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class BookmarkControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookmarkService bookmarkService;

    @MockBean
    private EventService eventService;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    void 유저가_북마크한_이벤트_조회에_성공한다() throws Exception {
        // given
        List<Event> events = List.of(event1(), showEvent(), clubEvent());
        Page<UserBookmarkedEventGetInfo> eventPages = new PageImpl<>(events).map(UserBookmarkedEventGetInfo::from);

        given(eventService.findAllBookmarkedEventPages(any(), any(Pageable.class))).willReturn(eventPages);

        // when, then
        mvc.perform(get("/api/v1/me/events/bookmark")
                        .header(AUTHORIZATION, "access token")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "id,asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(events.size()))
                .andExpect(jsonPath("$.pageData.first").value(true))
                .andExpect(jsonPath("$.pageData.last").value(true))
                .andExpect(jsonPath("$.pageData.pageNumber").value(0))
                .andExpect(jsonPath("$.pageData.size").value(3))
                .andExpect(jsonPath("$.pageData.totalPages").value(1))
                .andExpect(jsonPath("$.pageData.totalElements").value(events.size()))
                .andDo(
                        document("user/getAllBookmarkedEvents",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                queryParameters(
                                        parameterWithName("page").optional().description("페이지"),
                                        parameterWithName("size").optional().description("페이지 내 개수"),
                                        parameterWithName("sort").optional().description("정렬 방법(ex. id,desc)")
                                ),
                                responseFields(
                                        fieldWithPath("data").type(ARRAY).description("페이지 내 이벤트 정보"),
                                        fieldWithPath("data[].id").type(NUMBER).description("이벤트 아이디"),
                                        fieldWithPath("data[].title").type(STRING).description("이벤트 제목"),
                                        fieldWithPath("data[].location").type(STRING).description("이벤트 위치"),
                                        fieldWithPath("data[].clubName").type(STRING).description("이벤트 주최자"),
                                        fieldWithPath("data[].posterImageUrl").type(STRING).description("포스터 URL"),
                                        fieldWithPath("data[].startDate").type(STRING).description("이벤트 시작일"),
                                        fieldWithPath("data[].bookmark").type(BOOLEAN).description("북마크 상태"),
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
    void 개별_행사_북마크_상태_변경에_성공한다() throws Exception {
        // given
        Long eventId = 1L;
        doNothing().when(bookmarkService).changeBookmarkStatus(any(UserBookmarkInfo.class));
        BookmarkedEventRequest bookmarkedEventRequest = new BookmarkedEventRequest(true);

        // when, then
        mvc.perform(patch("/api/v1/me/events/{eventId}", eventId)
                        .header(AUTHORIZATION, "Access Token")
                        .content(mapper.writeValueAsString(bookmarkedEventRequest))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                ).andExpect(status().isOk())
                .andDo(
                        document("user/bookmark",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("eventId").description("행사 아이디")
                                ),
                                requestFields(
                                        fieldWithPath("bookmark").type(BOOLEAN).description("북마크 상태")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 유저가_북마크한_여부_조회에_성공한다() throws Exception {
        // given
        boolean bookmarkStatus = true;
        given(bookmarkService.isBookmarked(any(), any(Long.class))).willReturn(bookmarkStatus);

        // when
        ResultActions actions = mvc.perform(get("/api/v1/me/events/{eventId}", 1L)
                .header(AUTHORIZATION, "Access Token"));

        // then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user/isBookmarked",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("eventId").description("이벤트 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        responseBody()
                ));
    }

}
