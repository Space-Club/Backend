package com.spaceclub.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.global.UserArgumentResolver;
import com.spaceclub.global.interceptor.JwtAuthorizationInterceptor;
import com.spaceclub.user.controller.dto.UserRequiredInfoRequest;
import com.spaceclub.user.service.AccountService;
import com.spaceclub.user.service.vo.UserLoginInfo;
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

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = LoginController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        JwtAuthorizationInterceptor.class,
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    void 유저가_신규유저이면_빈값_토큰과_아이디반환에_성공한다() throws Exception {
        //given
        UserLoginInfo loginInfo = UserLoginInfo.from(1L, "", "");
        given(accountService.loginUser(any())).willReturn(loginInfo);

        // when, then
        mvc.perform(post("/api/v1/users/oauths")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("code", "123456789")))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("userId").value(1L))
                .andExpect(jsonPath("accessToken").isEmpty())
                .andExpect(jsonPath("refreshToken").isEmpty())
                .andDo(
                        document("user/newKakaoLoginUser",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("code").type(STRING).description("카카오 로그인 코드")
                                ),
                                responseFields(
                                        fieldWithPath("userId").type(NUMBER).description("유저 ID"),
                                        fieldWithPath("accessToken").type(STRING).description("access token"),
                                        fieldWithPath("refreshToken").type(STRING).description("refresh token")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 유저가_신규유저가_아니면_생성된_토큰과_아이디반환에_성공한다() throws Exception {
        //given
        UserLoginInfo loginInfo = UserLoginInfo.from(1L, "access token", "refresh token");
        given(accountService.loginUser(any())).willReturn(loginInfo);

        // when, then
        mvc.perform(post("/api/v1/users/oauths")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("code", "123456789")))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("userId").value(loginInfo.userId()))
                .andExpect(jsonPath("accessToken").value(loginInfo.accessToken()))
                .andExpect(jsonPath("refreshToken").value(loginInfo.refreshToken()))
                .andDo(
                        document("user/existingKakaoLoginUser",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("code").type(STRING).description("카카오 로그인 코드")
                                ),
                                responseFields(
                                        fieldWithPath("userId").type(NUMBER).description("유저 ID"),
                                        fieldWithPath("accessToken").type(STRING).description("access token"),
                                        fieldWithPath("refreshToken").type(STRING).description("refresh token")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 필수정보를_입력하면_회원가입에_성공한다() throws Exception {
        //given
        UserRequiredInfoRequest request = new UserRequiredInfoRequest(2L, "name", "010-1234-5678");
        UserLoginInfo loginInfo = UserLoginInfo.from(1L, "access token", "refresh token");
        given(accountService.createAccount(any())).willReturn(loginInfo);
        // when, then
        mvc.perform(post("/api/v1/users")
                        .content(mapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("userId").value(loginInfo.userId()))
                .andExpect(jsonPath("accessToken").value(loginInfo.accessToken()))
                .andExpect(jsonPath("refreshToken").value(loginInfo.refreshToken()))
                .andDo(
                        document("user/createUser",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("userId").type(NUMBER).description("유저 ID"),
                                        fieldWithPath("name").type(STRING).description("유저 이름"),
                                        fieldWithPath("phoneNumber").type(STRING).description("유저 핸드폰 번호")
                                ),
                                responseHeaders(
                                        headerWithName("location").description("생성된 유저의 URI")
                                ),
                                responseFields(
                                        fieldWithPath("userId").type(NUMBER).description("유저 ID"),
                                        fieldWithPath("accessToken").type(STRING).description("access token"),
                                        fieldWithPath("refreshToken").type(STRING).description("refresh token")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 유저의_로그아웃에_성공한다() throws Exception {
        // given
        doNothing().when(accountService).logout(any(Long.class));

        // when, then
        mvc.perform(post("/api/v1/users/logout")
                        .header(AUTHORIZATION, "Access Token")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(document("user/logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 유저_회원_탈퇴에_성공한다() throws Exception {
        // given
        doNothing().when(accountService).deleteUser(any(Long.class));

        // when, then
        mvc.perform(delete("/api/v1/users")
                        .header(AUTHORIZATION, "Access Token")
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(document("user/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        )
                ));
    }

}
