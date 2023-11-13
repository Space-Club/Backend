package com.spaceclub.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.event.controller.dto.EventApplyRequest;
import com.spaceclub.event.controller.dto.EventCreateRequest;
import com.spaceclub.event.domain.Category;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.service.EventService;
import com.spaceclub.global.S3ImageUploader;
import com.spaceclub.global.jwt.service.JwtService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.event2;
import static com.spaceclub.event.EventTestFixture.event3;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
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
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class EventControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private S3ImageUploader uploader;

    private final EventCreateRequest eventCreateRequest = new EventCreateRequest(
            Category.SHOW,
            1L,
            new EventCreateRequest.EventInfoRequest(
                    "행사 제목",
                    "행사 내용",
                    LocalDate.of(2023, 11, 15),
                    LocalTime.of(14, 0),
                    "행사 장소",
                    100
            ),
            new EventCreateRequest.TicketInfoRequest(20000, 2),
            new EventCreateRequest.BankInfoRequest("은행 명", "은행 계좌번호"),
            new EventCreateRequest.FormInfoRequest(
                    LocalDate.of(2023, 11, 1),
                    LocalTime.of(9, 0),
                    LocalDate.of(2023, 11, 10),
                    LocalTime.of(18, 0)
            )
    );

    @Test
    @WithMockUser
    public void 행사_생성에_성공한다() throws Exception {
        // given
        MockMultipartFile posterImage = new MockMultipartFile(
                "posterImage",
                "image.png",
                IMAGE_JPEG_VALUE,
                "<<jpeg data>>".getBytes());

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(eventCreateRequest)
        );

        final String posterImageUrl = "image.jpeg";
        given(uploader.uploadPosterImage(any(MultipartFile.class))).willReturn(posterImageUrl);
        given(eventService.create(any(Event.class), any(Long.class))).willReturn(1L);

        // when
        ResultActions actions = mvc.perform(multipart("/api/v1/events")
                .file(posterImage)
                .file(request)
                .with(csrf())
                .contentType(MULTIPART_FORM_DATA_VALUE)
        );

        // then
        actions
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/v1/events/1"))
                .andDo(document("event/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("request").description("행사 생성 관련 정보")
                                        .attributes(key("content-type").value(APPLICATION_JSON_VALUE)),
                                partWithName("posterImage").description("포스터 사진")
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE))
                        ),
                        requestPartFields("request",
                                fieldWithPath("category").type(STRING).description("카테고리"),
                                fieldWithPath("clubId").type(NUMBER).description("클럽 id"),
                                fieldWithPath("eventInfo.title").type(STRING).description("행사 정보"),
                                fieldWithPath("eventInfo.content").type(STRING).description("행사 내용"),
                                fieldWithPath("eventInfo.startDate").type(STRING).description("행사 날짜"),
                                fieldWithPath("eventInfo.startTime").type(STRING).description("행사 시간"),
                                fieldWithPath("eventInfo.location").type(STRING).description("행사 장소"),
                                fieldWithPath("eventInfo.capacity").type(NUMBER).description("행사 정원"),
                                fieldWithPath("ticketInfo.cost").type(NUMBER).description("행사 비용"),
                                fieldWithPath("ticketInfo.maxTicketCount").type(NUMBER).description("인당 예매 가능 수"),
                                fieldWithPath("bankInfo.name").type(STRING).description("은행 명"),
                                fieldWithPath("bankInfo.accountNumber").type(STRING).description("은행 계좌 번호"),
                                fieldWithPath("formInfo.openDate").type(STRING).description("폼 오픈 날짜"),
                                fieldWithPath("formInfo.openTime").type(STRING).description("폼 오픈 시간"),
                                fieldWithPath("formInfo.closeDate").type(STRING).description("폼 마감 날짜"),
                                fieldWithPath("formInfo.closeTime").type(STRING).description("폼 마감 시간")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("해당 행사 조회 URI")
                        )
                ));
    }

    @Test
    @WithMockUser
    public void 전체_행사_조회에_성공한다() throws Exception {
        // given
        List<Event> events = List.of(event1(), event2(), event3());
        Page<Event> eventPages = new PageImpl<>(events);

        given(eventService.getAll(any(Pageable.class))).willReturn(eventPages);

        // when
        ResultActions actions = mvc.perform(get("/api/v1/events")
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
                .andDo(document("event/getAll",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
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
                                fieldWithPath("data[].clubLogoImageUrl").type(STRING).description("클럽 로그 이미지 Url"),
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
    void 행사_신청에_성공한다() throws Exception {
        // given
        doNothing().when(eventService).applyEvent(any(Long.class), any(Long.class));
        given(jwtService.verifyUserId(any())).willReturn(1L);

        Long eventId = 1L;
        EventApplyRequest request = EventApplyRequest.builder()
                .eventId(eventId)
                .build();

        // when
        ResultActions result = mvc.perform(post("/api/v1/events/apply")
                .header("Authorization", "access token")
                .content(mapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON_VALUE)
                .with(csrf()));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("event/apply",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("eventId").type(NUMBER).description("이벤트 ID")
                        )));
    }

    @Test
    @WithMockUser
    void 행사_상세_조회에_성공한다() throws Exception {
        // given
        Event event = event1();

        given(eventService.get(any(Long.class))).willReturn(event);

        // when
        ResultActions actions = mvc.perform(get("/api/v1/events/{eventId}", 1L));

        // then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("event/get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("eventId").description("행사 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("행사 ID"),
                                fieldWithPath("title").type(STRING).description("행사 제목"),
                                fieldWithPath("posterImageUrl").type(STRING).description("행사 포스터 URL"),
                                fieldWithPath("startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("startTime").type(STRING).description("행사 시작 시각"),
                                fieldWithPath("location").type(STRING).description("행사 위치"),
                                fieldWithPath("clubName").type(STRING).description("행사 주최 클럽 이름"),
                                fieldWithPath("clubLogoImageUrl").type(STRING).description("행사 주최 클럽 로고 이미지 Url"),
                                fieldWithPath("formOpenDateTime").type(STRING).description("행사 참여 신청 시작 날짜와 시간"),
                                fieldWithPath("formCloseDateTime").type(STRING).description("행사 참여 신청 종료 날짜와 시간")
                        )));
    }

    @Test
    @WithMockUser
    void 행사_참여_신청_취소에_성공한다() throws Exception {
        // given
        doNothing().when(eventService).cancelEvent(any(Long.class), any(Long.class));

        // when
        ResultActions actions = mvc.perform(delete("/api/v1/events/{eventId}/cancel", 1L)
                .header(AUTHORIZATION, "Access Token")
                .with(csrf())
        );

        // then
        actions.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("event/cancel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("eventId").description("행사 id")
                        )
                ));
    }

}
