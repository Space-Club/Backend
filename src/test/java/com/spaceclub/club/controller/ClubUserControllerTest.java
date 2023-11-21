package com.spaceclub.club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.controller.dto.ClubUserUpdateRequest;
import com.spaceclub.club.controller.dto.MemberGetResponse;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.club.service.ClubMemberManagerService;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.global.UserArgumentResolver;
import com.spaceclub.global.interceptor.JwtAuthorizationInterceptor;
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

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.club.domain.ClubUserRole.MANAGER;
import static com.spaceclub.user.UserTestFixture.user1;
import static com.spaceclub.user.UserTestFixture.user2;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = ClubUserController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        JwtAuthorizationInterceptor.class,
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class ClubUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ClubMemberManagerService clubMemberManagerService;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    void 클럽_유저_권한_조회에_성공한다() throws Exception {
        // given
        final String userRole = "MANAGER";
        given(clubMemberManagerService.getUserRole(any(Long.class), any())).willReturn(userRole);

        // when, then
        mockMvc.perform(get("/api/v1/clubs/{clubId}/users", club1().getId())
                        .header(AUTHORIZATION, "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MANAGER"))
                .andDo(
                        document("club/getUserRole",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("clubId").description("클럽 아이디")),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("Access Token")
                                ),
                                responseFields(
                                        fieldWithPath("role").type(STRING).description("유저 권한")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    public void 클럽_멤버_조회에_성공한다() throws Exception {
        // given

        MemberGetResponse memberGet1 = MemberGetResponse.builder()
                .id(1L)
                .name(user1().getName())
                .profileImageUrl(user1().getProfileImageUrl())
                .role(ClubUserRole.MANAGER)
                .build();

        MemberGetResponse memberGet2 = MemberGetResponse.builder()
                .id(2L)
                .name(user2().getName())
                .profileImageUrl(user2().getProfileImageUrl())
                .role(ClubUserRole.MANAGER)
                .build();

        given(clubMemberManagerService.getMembers(any(Long.class), any()))
                .willReturn(List.of(memberGet1, memberGet2));

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/clubs/{clubId}/members", club1().getId())
                .header(AUTHORIZATION, "access token")
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document("club/getAllMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")),
                        responseFields(
                                fieldWithPath("[]").type(ARRAY).description("멤버 리스트"),
                                fieldWithPath("[].id").type(NUMBER).description("멤버의 유저 id"),
                                fieldWithPath("[].name").type(STRING).description("멤버 이름"),
                                fieldWithPath("[].profileImageUrl").type(STRING).description("멤버 이미지 Url"),
                                fieldWithPath("[].role").type(STRING).description("멤버 권한")

                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_멤버_권한_수정에_성공한다() throws Exception {
        // given
        Long clubId = 1L;
        Long memberId = 1L;
        ClubUserUpdateRequest request = new ClubUserUpdateRequest(MANAGER);

        doNothing().when(clubMemberManagerService).updateMemberRole(any(ClubUserUpdate.class));

        // when
        ResultActions result = this.mockMvc.perform(patch("/api/v1/clubs/{clubId}/members/{memberId}", clubId, memberId)
                .header(AUTHORIZATION, "access token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("club/updateMemberRole",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디"),
                                parameterWithName("memberId").description("멤버의 유저 id")
                        ),
                        requestFields(
                                fieldWithPath("role").description("멤버 권한")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_멤버_추방에_성공한다() throws Exception {
        // given
        Long clubId = 1L;
        Long memberId = 1L;

        doNothing().when(clubMemberManagerService).deleteMember(any(Long.class), any(Long.class), any(Long.class));

        // when
        ResultActions result = this.mockMvc.perform(delete("/api/v1/clubs/{clubId}/members/{memberId}", clubId, memberId)
                .header(AUTHORIZATION, "access token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("club/deleteMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디"),
                                parameterWithName("memberId").description("멤버의 유저 id")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_멤버일_경우_클럽_탈퇴에_성공한다() throws Exception {
        doNothing().when(clubMemberManagerService).exitClub(any(Long.class), any(Long.class));
        Long clubId = 1L;
        mockMvc.perform(delete("/api/v1/clubs/{clubId}/users", clubId)
                        .header(AUTHORIZATION, "access token")
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(document("club/exitClub",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        )
                ));
    }


}
