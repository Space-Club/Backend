package com.spaceclub.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.global.UserArgumentResolver;
import com.spaceclub.global.interceptor.AuthenticationInterceptor;
import com.spaceclub.global.interceptor.AuthorizationInterceptor;
import com.spaceclub.user.controller.dto.UserProfileUpdateRequest;
import com.spaceclub.user.service.AccountService;
import com.spaceclub.user.service.UserService;
import com.spaceclub.user.service.vo.RequiredProfile;
import com.spaceclub.user.service.vo.UserLoginInfo;
import com.spaceclub.user.service.vo.UserProfile;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        AuthorizationInterceptor.class,
                        AuthenticationInterceptor.class
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    void 유저의_프로필_조회에_성공한다() throws Exception {
        //given
        UserProfile userProfile = new UserProfile("멤버명", "010-1234-5678", "www.image.com");

        given(userService.getProfile(any())).willReturn(userProfile);

        // when, then
        mvc.perform(get("/api/v1/me/profile")
                        .header(AUTHORIZATION, "access token")
                )
                .andExpect(status().isOk())
                .andDo(
                        document("user/getUserProfile",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("access token")
                                ),
                                responseFields(
                                        fieldWithPath("username").type(STRING).description("유저 이름"),
                                        fieldWithPath("phoneNumber").type(STRING).description("유저 전화번호"),
                                        fieldWithPath("profileImageUrl").type(STRING).description("유저 프로필 이미지 URL")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 유저의_프로필_필수정보_수정에_성공한다() throws Exception {
        //given
        UserProfileUpdateRequest request = new UserProfileUpdateRequest("멤버명1", "010-1234-6789");
        UserLoginInfo userLoginInfo = new UserLoginInfo(1L, "access token", "refresh token");

        doNothing().when(userService).updateRequiredProfile(any(Long.class), any(RequiredProfile.class));
        given(accountService.createAccount(any())).willReturn(userLoginInfo);

        // when, then
        mvc.perform(put("/api/v1/me/profile")
                        .header(AUTHORIZATION, "access token")
                        .content(mapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(
                        document("user/changeUserProfile",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("name").type(STRING).description("유저 이름"),
                                        fieldWithPath("phoneNumber").type(STRING).description("유저 전화번호")
                                ),
                                responseFields(
                                        fieldWithPath("userId").type(NUMBER).description("유저 ID"),
                                        fieldWithPath("accessToken").type(STRING).description("액세스 토큰"),
                                        fieldWithPath("refreshToken").type(STRING).description("리프레시 토큰")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 유저의_이미지_변경에_성공한다() throws Exception {
        // given
        doNothing().when(userService).changeUserProfileImage(any(MultipartFile.class), any(Long.class));

        MockMultipartFile userImage = new MockMultipartFile(
                "userImage",
                "image.png",
                IMAGE_JPEG_VALUE,
                UUID.randomUUID().toString().getBytes()
        );

        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/api/v1/me/profile");
        requestBuilder.with(request -> {
            request.setMethod(PATCH.name());
            return request;
        });

        // when, then
        mvc.perform(requestBuilder
                        .file(userImage)
                        .contentType(MULTIPART_FORM_DATA_VALUE)
                        .header(AUTHORIZATION, "Access Token")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(
                        document("user/changeProfileImage",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParts(
                                        partWithName("userImage").description("유저 프로필 이미지")
                                ),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰"),
                                        headerWithName(CONTENT_TYPE).description(MULTIPART_FORM_DATA_VALUE)
                                )
                        )
                );
    }

}
