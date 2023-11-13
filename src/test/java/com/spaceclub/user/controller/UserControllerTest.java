package com.spaceclub.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.event.controller.dto.BookmarkedEventRequest;
import com.spaceclub.event.domain.Event;
import com.spaceclub.global.jwt.service.JwtService;
import com.spaceclub.user.UserTestFixture;
import com.spaceclub.user.controller.dto.UserProfileUpdateRequest;
import com.spaceclub.user.controller.dto.UserRequiredInfoRequest;
import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.service.UserService;
import com.spaceclub.user.service.vo.UserBookmarkInfo;
import com.spaceclub.user.service.vo.UserProfileInfo;
import com.spaceclub.user.service.vo.UserRequiredInfo;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.club.ClubTestFixture.club2;
import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.event2;
import static com.spaceclub.event.EventTestFixture.event3;
import static com.spaceclub.user.domain.Status.NOT_REGISTERED;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
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
    private JwtService jwtService;

    @Test
    @WithMockUser
    void 유저의_모든_이벤트_조회에_성공한다() throws Exception {
        // given
        List<Event> events = List.of(event1(), event2(), event3());
        Page<Event> eventPages = new PageImpl<>(events);

        given(userService.findAllEventPages(any(Long.class), any(Pageable.class))).willReturn(eventPages);
        given(userService.findEventStatus(any(Long.class), any(Event.class))).willReturn("CONFIRMED");

        // when, then
        mvc.perform(get("/api/v1/users/events")
                        .header("Authorization", "access token")
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
                        document("user/getAllEvents",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName("Authorization").description("액세스 토큰")
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

    @Test
    @WithMockUser
    void 유저가_신규유저이면_빈값_토큰과_아이디반환에_성공한다() throws Exception {
        //given
        User newUser = User.builder()
                .id(1L)
                .name(null)
                .phoneNumber(null)
                .status(NOT_REGISTERED)
                .oauthId("12345678")
                .provider(Provider.KAKAO)
                .email("abc@naver.com")
                .refreshToken("refreshToken")
                .profileImageUrl("www.image.com")
                .build();
        given(userService.createKakaoUser(any())).willReturn(newUser);

        // when, then
        mvc.perform(post("/api/v1/users/oauths")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("code", "123456789")))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("userId").value(newUser.getId()))
                .andExpect(jsonPath("accessToken").isEmpty())
                .andDo(
                        document("user/kakaoLoginNewUser",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("code").type(STRING).description("카카오 로그인 코드")
                                ),
                                responseFields(
                                        fieldWithPath("userId").type(NUMBER).description("유저 ID"),
                                        fieldWithPath("accessToken").type(STRING).description("access token")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 유저가_신규유저가_아니면_생성된_토큰과_아이디반환에_성공한다() throws Exception {
        //given
        User newUser = UserTestFixture.user2();
        given(userService.createKakaoUser(any())).willReturn(newUser);
        final String accessToken = "generated access token";
        given(jwtService.createToken(any(Long.class), any(String.class))).willReturn(accessToken);

        // when, then
        mvc.perform(post("/api/v1/users/oauths")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("code", "123456789")))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("userId").value(newUser.getId()))
                .andExpect(jsonPath("accessToken").value(accessToken))
                .andDo(
                        document("user/kakaoLoginExistingUser",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("code").type(STRING).description("카카오 로그인 코드")
                                ),
                                responseFields(
                                        fieldWithPath("userId").type(NUMBER).description("유저 ID"),
                                        fieldWithPath("accessToken").type(STRING).description("access token")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 필수정보를_입력하면_회원가입에_성공한다() throws Exception {
        //given
        UserRequiredInfoRequest request = new UserRequiredInfoRequest(2L, "name", "010-1234-5678");
        final User savedUser = UserTestFixture.user2();
        final String accessToken = "generated access token";
        given(userService.updateRequiredInfo(any(), any(UserRequiredInfo.class))).willReturn(savedUser);
        given(jwtService.createToken(any(Long.class), any(String.class))).willReturn(accessToken);

        // when, then
        mvc.perform(post("/api/v1/users")
                        .content(mapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("userId").value(savedUser.getId()))
                .andExpect(jsonPath("accessToken").value(accessToken))
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
                                        fieldWithPath("accessToken").type(STRING).description("access token")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 유저의_프로필_조회에_성공한다() throws Exception {
        //given
        final User user = UserTestFixture.user1();
        UserProfileInfo userProfileInfo = new UserProfileInfo("멤버명", "010-1234-5678", "www.image.com");

        given(jwtService.verifyUserId(any())).willReturn(user.getId());
        given(userService.getUserProfile(any())).willReturn(userProfileInfo);

        // when, then
        mvc.perform(get("/api/v1/users/profiles")
                        .header("Authorization", "access token")
                )
                .andExpect(status().isOk())
                .andDo(
                        document("user/getUserProfile",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName("Authorization").description("access token")
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
    void 유저의_프로필_수정에_성공한다() throws Exception {
        //given
        final User user = UserTestFixture.user1();
        UserProfileUpdateRequest request = new UserProfileUpdateRequest("멤버명1", "010-1234-6789");

        given(jwtService.verifyUserId(any())).willReturn(user.getId());
        given(userService.updateRequiredInfo(any(Long.class), any(UserRequiredInfo.class))).willReturn(user);

        // when, then
        mvc.perform(patch("/api/v1/users/required-infos")
                        .header(AUTHORIZATION, "access token")
                        .content(mapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andDo(
                        document("user/updateUserProfile",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("access token")
                                ),
                                requestFields(
                                        fieldWithPath("name").type(STRING).description("유저 이름"),
                                        fieldWithPath("phoneNumber").type(STRING).description("유저 전화번호")
                                )
                        )
                );
    }


    @Test
    @WithMockUser
    void 유저_프로필_이미지_조회에_성공한다() throws Exception {
        //given
        final User user = UserTestFixture.user1();
        final String profileImageUrl = "www.image.com";

        given(jwtService.verifyUserId(any())).willReturn(user.getId());
        given(userService.getUserProfileImage(any())).willReturn(profileImageUrl);

        // when, then
        mvc.perform(get("/api/v1/users/images")
                        .header("Authorization", "access token")
                )
                .andExpect(status().isOk())
                .andDo(
                        document("user/getUserProfileImage",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName("Authorization").description("access token")
                                ),
                                responseFields(
                                        fieldWithPath("profileImageUrl").type(STRING).description("유저 프로필 이미지 URL")
                                )
                        )
                );
    }


    @Test
    @WithMockUser
    void 유저의_모든_클럽_조회에_성공한다() throws Exception {
        // given
        given(jwtService.verifyUserId(any())).willReturn(1L);
        given(userService.getClubs(1L)).willReturn(List.of(club1(), club2()));

        // when
        ResultActions actions = mvc.perform(get("/api/v1/users/clubs")
                .header("Authorization", "access token")
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user/getAllClubs",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("[]").type(ARRAY).description("클럽"),
                                fieldWithPath("[].id").type(NUMBER).description("클럽 아이디"),
                                fieldWithPath("[].logoImageUrl").type(STRING).description("클럽 이미지 Url"),
                                fieldWithPath("[].name").type(STRING).description("클럽 이름")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 유저가_북마크한_이벤트_조회에_성공한다() throws Exception {
        // given
        List<Event> events = List.of(event1(), event2(), event3());
        Page<Event> eventPages = new PageImpl<>(events);

        Long userId = 1L;
        given(jwtService.verifyUserId(any())).willReturn(userId);
        given(userService.findAllBookmarkedEventPages(any(Long.class), any(Pageable.class))).willReturn(eventPages);

        // when, then
        mvc.perform(get("/api/v1/users/bookmarked-events")
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
        Long userId = 1L;
        given(jwtService.verifyUserId(any())).willReturn(userId);
        doNothing().when(userService).changeBookmarkStatus(any(UserBookmarkInfo.class));
        BookmarkedEventRequest bookmarkedEventRequest = new BookmarkedEventRequest(true);

        // when, then
        mvc.perform(patch("/api/v1/users/events/{eventId}", 1L)
                        .header(AUTHORIZATION, "Access Token")
                        .content(mapper.writeValueAsString(bookmarkedEventRequest))
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                ).andExpect(status().isOk())
                .andDo(document("user/bookmark",
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
                ));
    }

    @Test
    @WithMockUser
    void 유저의_로그아웃에_성공한다() throws Exception {
        // given
        Long userId = 1L;
        given(jwtService.verifyUserId(any())).willReturn(userId);
        doNothing().when(userService).logout(any(Long.class));

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
        Long userId = 1L;
        given(jwtService.verifyUserId(any())).willReturn(userId);
        doNothing().when(userService).deleteUser(any(Long.class));

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
