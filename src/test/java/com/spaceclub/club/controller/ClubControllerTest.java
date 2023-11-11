package com.spaceclub.club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.controller.dto.ClubCreateRequest;
import com.spaceclub.club.controller.dto.ClubNoticeCreateRequest;
import com.spaceclub.club.controller.dto.ClubUpdateRequest;
import com.spaceclub.club.controller.dto.ClubUserUpdateRequest;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.event.domain.Event;
import com.spaceclub.global.S3ImageUploader;
import com.spaceclub.global.jwt.service.JwtService;
import com.spaceclub.invite.service.InviteService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.club.ClubUserTestFixture.club1User1Manager;
import static com.spaceclub.club.ClubUserTestFixture.club1User2Manager;
import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.event2;
import static com.spaceclub.event.EventTestFixture.event3;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClubController.class)
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
    private S3ImageUploader uploader;

    @MockBean
    private InviteService inviteService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void 클럽_생성에_성공한다() throws Exception {
        // given
        given(clubService.createClub(any(Club.class), any(Long.class))).willReturn(
                Club.builder()
                        .id(1L)
                        .name("연사모")
                        .info("연어를 사랑하는 모임")
                        .logoImageUrl("연어.jpg")
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
                .header("Authorization", "token")
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
                                headerWithName("Authorization").description("유저 액세스 토큰")
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
        given(clubService.getClub(any(Long.class))).willReturn(club1());

        // when
        ResultActions result = this.mockMvc.perform(get("/api/v1/clubs/{clubId}", club1().getId())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("club/get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")),
                        responseFields(
                                fieldWithPath("name").type(STRING).description("클럽 이름"),
                                fieldWithPath("logoImageUrl").type(STRING).description("클럽 이미지 Url"),
                                fieldWithPath("info").type(STRING).description("클럽 소개"),
                                fieldWithPath("memberCount").type(NUMBER).description("클럽 멤버 수"),
                                fieldWithPath("coverImageUrl").type(STRING).description("클럽 커버 이미지 Url"),
                                fieldWithPath("inviteLink").type(STRING).description("클럽 초대 링크")
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

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsString(clubCreateRequest).getBytes(StandardCharsets.UTF_8)
        );

        // when
        ResultActions result = this.mockMvc.perform(multipart("/api/v1/clubs/{clubId}", clubId)
                .file(request)
                .file(logoImage)
                .header("Authorization", "token")
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
                                partWithName("request").description("클럽 수정 요청 DTO"),
                                partWithName("logoImage").description("클럽 썸네일 이미지")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("유저 액세스 토큰")
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
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("club/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @WithMockUser
    public void 클럽_행사_조회에_성공한다() throws Exception {
        // given
        List<Event> events = List.of(event1(), event2(), event3());
        Page<Event> eventPages = new PageImpl<>(events);

        given(clubService.getClubEvents(any(Long.class), any(Pageable.class))).willReturn(eventPages);
        Long clubId = 1L;

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/clubs/{clubId}/events", clubId)
                .param("page", "1")
                .param("size", "3")
                .param("sort", "id,asc")
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(events.size()))
                .andExpect(jsonPath("$.pageData.first").value(true))
                .andExpect(jsonPath("$.pageData.last").value(true))
                .andExpect(jsonPath("$.pageData.pageNumber").value(0))
                .andExpect(jsonPath("$.pageData.size").value(3))
                .andExpect(jsonPath("$.pageData.totalPages").value(1))
                .andExpect(jsonPath("$.pageData.totalElements").value(events.size()))
                .andDo(document("club/getAllEvent",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")),
                        queryParameters(
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("페이지 내 개수"),
                                parameterWithName("sort").description("정렬 방법((ex) id,desc)")
                        ),
                        responseFields(
                                fieldWithPath("data").type(ARRAY).description("페이지 내 행사 정보"),
                                fieldWithPath("data[].id").type(NUMBER).description("행사 아이디"),
                                fieldWithPath("data[].title").type(STRING).description("행사 제목"),
                                fieldWithPath("data[].posterImageUrl").type(STRING).description("포스터 URL"),
                                fieldWithPath("data[].location").type(STRING).description("행사 위치"),
                                fieldWithPath("data[].startDate").type(STRING).description("행사 날짜"),
                                fieldWithPath("data[].startTime").type(STRING).description("행사 시간"),
                                fieldWithPath("data[].location").type(STRING).description("행사 위치"),
                                fieldWithPath("data[].clubName").type(STRING).description("클럽 명"),
                                fieldWithPath("data[].clubLogoImageUrl").type(STRING).description("클럽 로고 이미지 Url"),
                                fieldWithPath("data[].openStatus").type(STRING).description("행사 공개 상태"),
                                fieldWithPath("pageData").type(OBJECT).description("페이지 정보"),
                                fieldWithPath("pageData.first").type(BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("pageData.last").type(BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("pageData.pageNumber").type(NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("pageData.size").type(NUMBER).description("페이지 내 개수"),
                                fieldWithPath("pageData.totalPages").type(NUMBER).description("총 페이지 개수"),
                                fieldWithPath("pageData.totalElements").type(NUMBER).description("총 행사 개수")
                        )
                ));
    }

    @Test
    @WithMockUser
    public void 클럽_멤버_조회에_성공한다() throws Exception {
        // given
        given(clubService.getMembers(any(Long.class))).willReturn(List.of(club1User1Manager(), club1User2Manager()));

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/clubs/{clubId}/members", club1().getId()));

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document("club/getAllMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")),
                        responseFields(
                                fieldWithPath("[]").type(ARRAY).description("멤버 리스트"),
                                fieldWithPath("[].id").type(NUMBER).description("멤버 아이디"),
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
        ClubUserUpdateRequest request = new ClubUserUpdateRequest(ClubUserRole.MANAGER);

        doNothing().when(clubService).updateMemberRole(any(ClubUserUpdate.class));

        // when
        ResultActions result = this.mockMvc.perform(patch("/api/v1/clubs/{clubId}/members/{memberId}", clubId, memberId)
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
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디"),
                                parameterWithName("memberId").description("멤버 아이디")
                        ),
                        requestFields(
                                fieldWithPath("role").description("멤버 권한")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_멤버_탈퇴에_성공한다() throws Exception {
        // given
        Long clubId = 1L;
        Long memberId = 1L;

        doNothing().when(clubService).deleteMember(any(Long.class), any(Long.class));

        // when
        ResultActions result = this.mockMvc.perform(delete("/api/v1/clubs/{clubId}/members/{memberId}", clubId, memberId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("club/deleteMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디"),
                                parameterWithName("memberId").description("멤버 아이디")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_공지사항_작성에_성공한다() throws Exception {
        // given
        Long clubId = 1L;

        ClubNoticeCreateRequest request = new ClubNoticeCreateRequest("notice");

        // when
        ResultActions result = this.mockMvc.perform(post("/api/v1/clubs/{clubId}/notices", clubId)
                .header("Authorization", "access token")
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
                                headerWithName("Authorization").description("유저 액세스 토큰")
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

        // when
        ResultActions result = this.mockMvc.perform(get("/api/v1/clubs/{clubId}/notices", clubId)
                .header("Authorization", "access token")
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("club/getNotice",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("유저 액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")
                        ),
                        responseFields(
                                fieldWithPath("notices").type(ARRAY).description("공지 사항 리스트")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_공지사항_수정에_성공한다() throws Exception {
        // given
        Long clubId = 1L;

        // when
        ResultActions result = this.mockMvc.perform(patch("/api/v1/clubs/{clubId}/notices", clubId)
                .header("Authorization", "access token")
                .with(csrf())
        );

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("club/updateNotice",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("유저 액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_공지사항_삭제에_성공한다() throws Exception {
        // given
        Long clubId = 1L;

        // when
        ResultActions result = this.mockMvc.perform(delete("/api/v1/clubs/{clubId}/notices", clubId)
                .header("Authorization", "access token")
                .with(csrf())
        );

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("club/deleteNotice",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("유저 액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 클럽_일정_조회에_성공한다() throws Exception {
        // given
        Long clubId = 1L;

        // when
        ResultActions result = this.mockMvc.perform(get("/api/v1/clubs/{clubId}/schedules", clubId)
                .header("Authorization", "access token")
                .with(csrf())
        );

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("club/getSchedule",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("유저 액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("클럽 아이디")
                        ),
                        responseFields(
                                fieldWithPath("schedules").type(ARRAY).description("클럽 일정"),
                                fieldWithPath("schedules.[].id").type(NUMBER).description("일정 ID"),
                                fieldWithPath("schedules.[].category").type(STRING).description("일정 종류 (클럽)"),
                                fieldWithPath("schedules.[].eventInfo").type(OBJECT).description("행사 정보"),
                                fieldWithPath("schedules.[].eventInfo.title").type(STRING).description("행사 제목"),
                                fieldWithPath("schedules.[].eventInfo.content").type(STRING).description("행사 내용"),
                                fieldWithPath("schedules.[].eventInfo.startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("schedules.[].eventInfo.location").type(STRING).description("행사 장소"),
                                fieldWithPath("schedules.[].eventInfo.capacity").type(NUMBER).description("행사 최대 인원수"),
                                fieldWithPath("schedules.[].eventInfo.posterImageUrl").type(STRING).description("행사 포스터 이미지 URL"),
                                fieldWithPath("schedules.[].club").type(OBJECT).description("클럽"),
                                fieldWithPath("schedules.[].club.id").type(NUMBER).description("클럽 ID"),
                                fieldWithPath("schedules.[].club.name").type(STRING).description("클럽 이름"),
                                fieldWithPath("schedules.[].club.logoImageUrl").type(STRING).description("로고 이미지 URL"),
                                fieldWithPath("schedules.[].club.coverImageUrl").type(STRING).description("커버 이미지 URL"),
                                fieldWithPath("schedules.[].club.info").type(STRING).description("클럽 소개"),
                                fieldWithPath("schedules.[].club.notices").type(ARRAY).description("클럽 공지사항"),
                                fieldWithPath("schedules.[].club.clubUser").type(ARRAY).description("클럽 멤버 목록"),
                                fieldWithPath("schedules.[].users").type(ARRAY).description("이벤트 참가 인원"),
                                fieldWithPath("schedules.[].form").type(OBJECT).description("폼"),
                                fieldWithPath("schedules.[].form.id").type(NUMBER).description("폼 ID"),
                                fieldWithPath("schedules.[].form.description").type(STRING).description("폼 설명"),
                                fieldWithPath("schedules.[].form.options").type(ARRAY).description("폼 옵션"),
                                fieldWithPath("schedules.[].location").type(STRING).description("행사 장소"),
                                fieldWithPath("schedules.[].clubId").type(NUMBER).description("클럽 ID"),
                                fieldWithPath("schedules.[].formId").type(NUMBER).description("폼 ID"),
                                fieldWithPath("schedules.[].title").type(STRING).description("행사 제목"),
                                fieldWithPath("schedules.[].startTime").type(STRING).description("행사 시작 시간"),
                                fieldWithPath("schedules.[].startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("schedules.[].clubName").type(STRING).description("클럽 이름"),
                                fieldWithPath("schedules.[].formOpenDate").type(STRING).description("폼 여는 시간"),
                                fieldWithPath("schedules.[].formCloseDate").type(STRING).description("폼 닫는 시간"),
                                fieldWithPath("schedules.[].clubLogoImageUrl").type(STRING).description("클럽 로고 이미지 URL"),
                                fieldWithPath("schedules.[].posterImageUrl").type(STRING).description("포스터 이미지 URL")
                        )
                ));
    }

}
