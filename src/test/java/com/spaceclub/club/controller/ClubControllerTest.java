package com.spaceclub.club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.controller.dto.ClubCreateRequest;
import com.spaceclub.club.controller.dto.ClubUpdateRequest;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.global.UserArgumentResolver;
import com.spaceclub.global.config.s3.S3Properties;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static com.spaceclub.club.ClubTestFixture.club1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = ClubController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        AuthorizationInterceptor.class,
                        AuthenticationInterceptor.class
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class ClubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ClubService clubService;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @MockBean
    private S3Properties s3Properties;

    @Test
    @WithMockUser
    void 클럽_생성에_성공한다() throws Exception {
        // given
        given(clubService.createClub(any(Club.class), any(), any(MultipartFile.class))).willReturn(
                Club.builder()
                        .id(1L)
                        .name("연사모")
                        .info("연어를 사랑하는 모임")
                        .logoImageName("연어.jpg")
                        .build()
        );

        ClubCreateRequest clubCreateRequest = new ClubCreateRequest(
                "연사모",
                "연어를 사랑하는 모임"
        );

        MockMultipartFile logoImage = new MockMultipartFile(
                "logoImage",
                "logoImage.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "<<jpeg data>>".getBytes()
        );

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsString(clubCreateRequest).getBytes(StandardCharsets.UTF_8)
        );

        // when
        ResultActions result = this.mockMvc.perform(multipart("/api/v1/clubs")
                .file(request)
                .file(logoImage)
                .header(AUTHORIZATION, "token")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(csrf())
        );

        // then
        result.andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andDo(print())
                .andDo(document("club/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("request").description("클럽 생성 요청 DTO"),
                                partWithName("logoImage").description("클럽 썸네일 이미지")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        requestPartFields(
                                "request",
                                fieldWithPath("name").type(STRING).description("클럽 이름"),
                                fieldWithPath("info").type(STRING).description("클럽 소개")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("생성된 클럽의 URI")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_조회에_성공한다() throws Exception {
        // given
        given(clubService.getClub(any(Long.class), any())).willReturn(club1());

        // when
        ResultActions result = this.mockMvc.perform(get("/api/v1/clubs/{clubId}", club1().getId())
                .header(AUTHORIZATION, "access token")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("club/get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저의 액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("name").type(STRING).description("클럽 이름"),
                                fieldWithPath("logoImageUrl").type(STRING).description("클럽 이미지 Url"),
                                fieldWithPath("info").type(STRING).description("클럽 소개"),
                                fieldWithPath("memberCount").type(NUMBER).description("클럽 멤버 수"),
                                fieldWithPath("coverImageUrl").type(STRING).description("클럽 커버 이미지 Url")
                        )));

    }

    @Test
    @WithMockUser
    void 클럽_수정에_성공한다() throws Exception {
        // given
        Long clubId = 1L;

        ClubUpdateRequest clubCreateRequest = new ClubUpdateRequest(
                "연사모",
                "연어를 사랑하는 모임"
        );

        MockMultipartFile logoImage = new MockMultipartFile(
                "logoImage",
                "logoImage.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "<<jpeg data>>".getBytes()
        );

        MockMultipartFile coverImage = new MockMultipartFile(
                "coverImage",
                "coverImage.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "<<jpeg data>>".getBytes()
        );

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsString(clubCreateRequest).getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/api/v1/clubs/{clubId}", clubId);
        requestBuilder.with(servletRequest -> {
            servletRequest.setMethod(HttpMethod.PATCH.name());
            return servletRequest;
        });

        // when
        ResultActions result = this.mockMvc.perform(requestBuilder
                .file(request)
                .file(logoImage)
                .file(coverImage)
                .header(AUTHORIZATION, "token")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(csrf())
        );

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("club/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("request").optional().description("클럽 수정 요청 DTO"),
                                partWithName("logoImage").optional().description("새로운 클럽 로고 이미지"),
                                partWithName("coverImage").optional().description("새로운 클럽 배경 이미지")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        requestPartFields(
                                "request",
                                fieldWithPath("name").type(STRING).description("클럽 이름"),
                                fieldWithPath("info").type(STRING).description("클럽 소개")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_삭제에_성공한다() throws Exception {
        // given
        Long clubId = 1L;

        // when
        ResultActions result = this.mockMvc.perform(delete("/api/v1/clubs/{clubId}", clubId)
                .header(AUTHORIZATION, "access token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("club/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        )
                ));
    }

}
