package com.spaceclub.invite.controller;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.domain.Club;
import com.spaceclub.global.UserArgumentResolver;
import com.spaceclub.global.interceptor.JwtAuthorizationInterceptor;
import com.spaceclub.invite.service.InviteService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.spaceclub.club.ClubTestFixture.club1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = InviteController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        JwtAuthorizationInterceptor.class,
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class InviteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InviteService inviteService;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    void 클럽_초대_링크_생성에_성공한다() throws Exception {
        // given
        Club club = club1();
        Long clubId = club.getId();

        given(inviteService.getInviteCode(any(Long.class), any())).willReturn("650d2d91-a8cf-45e7-8a43-a0c798173ecb");

        // when
        ResultActions actions = mockMvc.perform(post("/api/v1/clubs/{clubId}/invite", clubId)
                .header(AUTHORIZATION, "token")
                .with(csrf()));

        // then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("invite/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("inviteLink").type(STRING).description("클럽 초대 링크")
                        )
                ));
    }

}
