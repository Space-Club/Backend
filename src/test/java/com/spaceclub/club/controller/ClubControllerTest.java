package com.spaceclub.club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.controller.dto.ClubCreateRequest;
import com.spaceclub.club.controller.dto.ClubUserUpdateRequest;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubNotice;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.event.domain.Event;
import com.spaceclub.global.S3ImageUploader;
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
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
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

    @Test
    @WithMockUser
    void 클럽_생성에_성공한다() throws Exception {
        // given
        given(clubService.createClub(any(Club.class))).willReturn(
                Club.builder()
                        .id(1L)
                        .name("연사모")
                        .info("연어를 사랑하는 모임")
                        .owner("연어대장")
                        .thumbnailUrl("연어.jpg")
                        .build());

        ClubCreateRequest clubCreateRequest = new ClubCreateRequest(
                "연사모",
                "연어를 사랑하는 모임",
                "연어대장"
        );

        MockMultipartFile thumbnail = new MockMultipartFile(
                "thumbnail",
                "thumbnail.jpg",
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
                .file(thumbnail)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(csrf())
        );

        // then
        result.andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/v1/clubs/1"))
                .andDo(print())
                .andDo(document("club/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("request").description("클럽 생성 요청 DTO"),
                                partWithName("thumbnail").description("클럽 썸네일 이미지")
                        ),
                        requestPartFields(
                                "request",
                                fieldWithPath("name").type(STRING).description("클럽 이름"),
                                fieldWithPath("info").type(STRING).description("클럽 소개"),
                                fieldWithPath("owner").type(STRING).description("클럽 생성자")
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
        given(clubService.getClub(any(Long.class))).willReturn(
                Club.builder()
                        .name("연사모")
                        .info("이곳은 연사모입니다")
                        .thumbnailUrl("연어.png")
                        .owner("연어대장")
                        .notices(List.of(new ClubNotice("연사모의 공지사항1")))
                        .build()
        );
        Long clubId = 1L;

        // when
        ResultActions result = this.mockMvc.perform(get("/api/v1/clubs/{clubId}", clubId)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("club/get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("name").type(STRING).description("클럽 이름"),
                                fieldWithPath("info").type(STRING).description("클럽 소개"),
                                fieldWithPath("memberCount").type(NUMBER).description("클럽 멤버수"),
                                fieldWithPath("image").type(STRING).description("클럽 썸네일 이미지"),
                                fieldWithPath("notices").type(ARRAY).description("클럽 공지사항 리스트")
                        )));
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
                                fieldWithPath("data[].poster").type(STRING).description("포스터 URL"),
                                fieldWithPath("data[].location").type(STRING).description("행사 위치"),
                                fieldWithPath("data[].startDate").type(STRING).description("행사 날짜"),
                                fieldWithPath("data[].startTime").type(STRING).description("행사 시간"),
                                fieldWithPath("data[].location").type(STRING).description("행사 위치"),
                                fieldWithPath("data[].clubName").type(STRING).description("클럽 명"),
                                fieldWithPath("data[].clubImage").type(STRING).description("클럽 이미지"),
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
                                fieldWithPath("[].image").type(STRING).description("멤버 이미지"),
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

}
