package com.spaceclub.club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.controller.dto.ClubNoticeCreateRequest;
import com.spaceclub.club.controller.dto.ClubNoticeUpdateRequest;
import com.spaceclub.club.service.ClubNoticeService;
import com.spaceclub.club.service.vo.ClubNoticeUpdate;
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

import static com.spaceclub.club.ClubNoticeTestFixture.clubNotice1;
import static com.spaceclub.club.ClubNoticeTestFixture.clubNotice2;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
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

@WebMvcTest(
        value = ClubNoticeController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        WebConfig.class,
                        AuthorizationInterceptor.class,
                        AuthenticationInterceptor.class
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class ClubNoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ClubNoticeService clubNoticeService;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    void 클럽_공지사항_작성에_성공한다() throws Exception {
        // given
        Long clubId = 1L;

        ClubNoticeCreateRequest request = new ClubNoticeCreateRequest("notice");

        // when
        ResultActions result = this.mockMvc.perform(post("/api/v1/clubs/{clubId}/notices", clubId)
                .header(AUTHORIZATION, "access token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .with(csrf())
        );

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("club/createNotice",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")
                        ),
                        requestFields(
                                fieldWithPath("notice").type(STRING).description("공지 사항")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_공지사항_조회에_성공한다() throws Exception {
        // given
        Long clubId = 1L;
        given(clubNoticeService.getNotices(any(Long.class), any())).willReturn(
                List.of(clubNotice1(), clubNotice2())
        );

        // when
        ResultActions result = this.mockMvc.perform(get("/api/v1/clubs/{clubId}/notices", clubId)
                .header(AUTHORIZATION, "access token")
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("club/getNotice",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")
                        ),
                        responseFields(
                                fieldWithPath("notices").type(ARRAY).description("공지 사항 리스트"),
                                fieldWithPath("notices.[].id").type(NUMBER).description("공지 사항 항목 ID"),
                                fieldWithPath("notices.[].notice").type(STRING).description("공지 사항 내용")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_공지사항_수정에_성공한다() throws Exception {
        // given
        Long clubId = 1L;
        Long noticeId = 1L;
        doNothing().when(clubNoticeService).updateNotice(any(ClubNoticeUpdate.class));

        // when
        ResultActions result = this.mockMvc.perform(patch("/api/v1/clubs/{clubId}/notices/{noticeId}", clubId, noticeId)
                .header(AUTHORIZATION, "access token")
                .content(mapper.writeValueAsString(new ClubNoticeUpdateRequest("새로운 공지사항")))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("club/updateNotice",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디"),
                                parameterWithName("noticeId").description("공지사항 아이디")
                        ),
                        requestFields(
                                fieldWithPath("notice").type(STRING).description("새로운 공지사항")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_공지사항_삭제에_성공한다() throws Exception {
        // given
        Long clubId = 1L;
        Long noticeId = 1L;

        // when
        ResultActions result = this.mockMvc.perform(delete("/api/v1/clubs/{clubId}/notices/{noticeId}", clubId, noticeId)
                .header(AUTHORIZATION, "access token")
                .with(csrf())
        );

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("club/deleteNotice",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디"),
                                parameterWithName("noticeId").description("공지사항 아이디")

                        )
                ));
    }

}
