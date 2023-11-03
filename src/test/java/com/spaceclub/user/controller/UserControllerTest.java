package com.spaceclub.user.controller;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.event.domain.Event;
import com.spaceclub.user.service.UserService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.event2;
import static com.spaceclub.event.EventTestFixture.event3;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

@WebMvcTest(UserController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    void 유저의_모든_이벤트_조회에_성공한다() throws Exception {
        // given
        final Long userId = 1L;
        List<Event> events = List.of(event1(), event2(), event3());
        Page<Event> eventPages = new PageImpl<>(events);

        given(userService.findAllEventPages(any(Long.class), any(Pageable.class))).willReturn(eventPages);
        given(userService.findEventStatus(any(Long.class), any(Event.class))).willReturn("CONFIRMED");

        // when, then
        mvc.perform(get("/api/v1/users/{userId}/events", userId)
                                .param("page", "1")
                                .param("size", "10")
                                .param("sort", "startDate,desc")
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
                        document("user/getAllEvents",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                queryParameters(
                                        parameterWithName("page").description("페이지"),
                                        parameterWithName("size").description("페이지 내 개수"),
                                        parameterWithName("sort").description("정렬 방법((ex) id,desc)")
                                ),
                                pathParameters(
                                        parameterWithName("userId").description("유저 아이디")),
                                responseFields(
                                        fieldWithPath("data").type(ARRAY).description("페이지 내 이벤트 정보"),
                                        fieldWithPath("data[].id").type(NUMBER).description("이벤트 아이디"),
                                        fieldWithPath("data[].title").type(STRING).description("이벤트 제목"),
                                        fieldWithPath("data[].location").type(STRING).description("이벤트 위치"),
                                        fieldWithPath("data[].clubName").type(STRING).description("이벤트 주최자"),
                                        fieldWithPath("data[].posterImageUrl").type(STRING).description("포스터 URL"),
                                        fieldWithPath("data[].startDate").type(STRING).description("이벤트 시작일"),
                                        fieldWithPath("data[].status").type(STRING).description("이벤트 상태"),
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

}
