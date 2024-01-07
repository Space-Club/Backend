package com.spaceclub.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.club.ClubTestFixture;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.service.ClubProvider;
import com.spaceclub.event.controller.dto.EventCreateRequest;
import com.spaceclub.event.controller.dto.EventUpdateRequest;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.service.EventImageService;
import com.spaceclub.event.service.EventService;
import com.spaceclub.event.service.UserEventService;
import com.spaceclub.event.service.vo.EventCreateInfo;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.spaceclub.event.EventTestFixture.clubEvent;
import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.promotionEvent;
import static com.spaceclub.event.EventTestFixture.recruitmentEvent;
import static com.spaceclub.event.EventTestFixture.showEvent;
import static com.spaceclub.event.domain.EventCategory.CLUB;
import static com.spaceclub.event.domain.EventCategory.PROMOTION;
import static com.spaceclub.event.domain.EventCategory.RECRUITMENT;
import static com.spaceclub.event.domain.EventCategory.SHOW;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = EventController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        WebConfig.class,
                        AuthorizationInterceptor.class,
                        AuthenticationInterceptor.class
                })
        })
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
    private UserEventService userEventService;

    @MockBean
    private ClubProvider clubProvider;

    @MockBean
    private EventImageService eventImageService;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    private final MockMultipartFile posterImage = new MockMultipartFile(
            "posterImage",
            "image.png",
            IMAGE_JPEG_VALUE,
            "<<jpeg data>>".getBytes());


    @Test
    @WithMockUser
    public void 공연_행사_생성에_성공한다() throws Exception {
        // given
        EventCreateRequest showEventCreateRequest = new EventCreateRequest(
                1L,
                EventCreateRequest.EventInfoRequest.builder()
                        .title("행사 제목")
                        .content("행사 내용")
                        .startDate(LocalDate.of(2023, 11, 15))
                        .startTime(LocalTime.of(14, 0))
                        .location("행사 장소")
                        .capacity(100)
                        .build(),
                EventCreateRequest.FormInfoRequest.builder()
                        .openDate(LocalDate.of(2023, 11, 1))
                        .openTime(LocalTime.of(9, 0))
                        .closeDate(LocalDate.of(2023, 11, 10))
                        .closeTime(LocalTime.of(18, 0))
                        .build(),
                EventCreateRequest.TicketInfoRequest.builder()
                        .cost(20000)
                        .maxTicketCount(2)
                        .build(),
                EventCreateRequest.BankInfoRequest.builder()
                        .name("은행 명")
                        .accountNumber("은행 계좌번호")
                        .build()
        );
        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(showEventCreateRequest)
        );

        MockMultipartFile category = new MockMultipartFile(
                "category",
                "",
                TEXT_PLAIN_VALUE,
                SHOW.name().getBytes(StandardCharsets.UTF_8)
        );

        Club club = ClubTestFixture.club1();
        given(clubProvider.getClub(any(Long.class))).willReturn(club);
        given(eventService.create(any(EventCreateInfo.class), any(Club.class))).willReturn(1L);

        // when
        ResultActions actions = mvc.perform(multipart("/api/v1/events")
                .file(posterImage)
                .file(request)
                .file(category)
                .header(AUTHORIZATION, "Access Token")
                .with(csrf())
                .contentType(MULTIPART_FORM_DATA_VALUE)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document("event/create/show",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("posterImage").description("포스터 사진")
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE)),
                                partWithName("request").description("행사 생성 관련 정보")
                                        .attributes(key("content-type").value(APPLICATION_JSON_VALUE)),
                                partWithName("category").description("행사 카테고리")
                                        .attributes(key("content-type").value(TEXT_PLAIN_VALUE))
                        ),
                        requestPartFields("request",
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
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("eventId").type(NUMBER).description("생성된 행사 id")
                        ))
                );
    }

    @Test
    @WithMockUser
    public void 홍보_행사_생성에_성공한다() throws Exception {
        // given
        EventCreateRequest promotionEventCreateRequest = EventCreateRequest.builder()
                .clubId(1L)
                .eventInfo(
                        EventCreateRequest.EventInfoRequest.builder()
                                .title("행사 제목")
                                .content("행사 내용")
                                .startDate(LocalDate.of(2023, 11, 15))
                                .startTime(LocalTime.of(14, 0))
                                .location("행사 장소")
                                .capacity(100)
                                .build())
                .formInfo(
                        EventCreateRequest.FormInfoRequest.builder()
                                .openDate(LocalDate.of(2023, 11, 1))
                                .openTime(LocalTime.of(9, 0))
                                .closeDate(LocalDate.of(2023, 11, 10))
                                .closeTime(LocalTime.of(18, 0))
                                .build()
                )
                .build();

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(promotionEventCreateRequest)
        );

        MockMultipartFile category = new MockMultipartFile(
                "category",
                "",
                TEXT_PLAIN_VALUE,
                PROMOTION.name().getBytes(StandardCharsets.UTF_8)
        );

        Club club = ClubTestFixture.club1();
        given(clubProvider.getClub(any(Long.class))).willReturn(club);
        given(eventService.create(any(EventCreateInfo.class), any(Club.class))).willReturn(1L);

        // when
        ResultActions actions = mvc.perform(multipart("/api/v1/events")
                .file(posterImage)
                .file(request)
                .file(category)
                .header(AUTHORIZATION, "Access Token")
                .with(csrf())
                .contentType(MULTIPART_FORM_DATA_VALUE)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document("event/create/promotion",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("posterImage").description("포스터 사진")
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE)),
                                partWithName("request").description("행사 생성 관련 정보")
                                        .attributes(key("content-type").value(APPLICATION_JSON_VALUE)),
                                partWithName("category").description("행사 카테고리")
                                        .attributes(key("content-type").value(TEXT_PLAIN_VALUE))
                        ),
                        requestPartFields("request",
                                fieldWithPath("clubId").type(NUMBER).description("클럽 id"),
                                fieldWithPath("eventInfo.title").type(STRING).description("행사 정보"),
                                fieldWithPath("eventInfo.content").type(STRING).description("행사 내용"),
                                fieldWithPath("eventInfo.startDate").type(STRING).description("행사 날짜"),
                                fieldWithPath("eventInfo.startTime").type(STRING).description("행사 시간"),
                                fieldWithPath("eventInfo.location").type(STRING).description("행사 장소"),
                                fieldWithPath("eventInfo.capacity").type(NUMBER).description("행사 정원"),
                                fieldWithPath("formInfo.openDate").type(STRING).description("폼 오픈 날짜"),
                                fieldWithPath("formInfo.openTime").type(STRING).description("폼 오픈 시간"),
                                fieldWithPath("formInfo.closeDate").type(STRING).description("폼 마감 날짜"),
                                fieldWithPath("formInfo.closeTime").type(STRING).description("폼 마감 시간")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("eventId").type(NUMBER).description("생성된 행사 id")
                        ))
                );
    }

    @Test
    @WithMockUser
    public void 모집_공고_행사_생성에_성공한다() throws Exception {
        // given
        EventCreateRequest recruitmentEventCreateRequest = EventCreateRequest.builder()
                .clubId(1L)
                .eventInfo(
                        EventCreateRequest.EventInfoRequest.builder()
                                .title("행사 제목")
                                .content("행사 내용")
                                .activityArea("활동 지역")
                                .recruitmentTarget("모집 대상")
                                .recruitmentLimit(100)
                                .build()
                )
                .formInfo(
                        EventCreateRequest.FormInfoRequest.builder()
                                .openDate(LocalDate.of(2023, 11, 1))
                                .openTime(LocalTime.of(9, 0))
                                .closeDate(LocalDate.of(2023, 11, 10))
                                .closeTime(LocalTime.of(18, 0))
                                .build()
                )
                .build();

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(recruitmentEventCreateRequest)
        );

        MockMultipartFile category = new MockMultipartFile(
                "category",
                "",
                TEXT_PLAIN_VALUE,
                RECRUITMENT.name().getBytes(StandardCharsets.UTF_8)
        );

        Club club = ClubTestFixture.club1();
        given(clubProvider.getClub(any(Long.class))).willReturn(club);
        given(eventService.create(any(EventCreateInfo.class), any(Club.class))).willReturn(1L);

        // when
        ResultActions actions = mvc.perform(multipart("/api/v1/events")
                .file(posterImage)
                .file(request)
                .file(category)
                .header(AUTHORIZATION, "Access Token")
                .with(csrf())
                .contentType(MULTIPART_FORM_DATA_VALUE)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document("event/create/recruitment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("posterImage").description("포스터 사진")
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE)),
                                partWithName("request").description("행사 생성 관련 정보")
                                        .attributes(key("content-type").value(APPLICATION_JSON_VALUE)),
                                partWithName("category").description("행사 카테고리")
                                        .attributes(key("content-type").value(TEXT_PLAIN_VALUE))
                        ),
                        requestPartFields("request",
                                fieldWithPath("clubId").type(NUMBER).description("클럽 id"),
                                fieldWithPath("eventInfo.title").type(STRING).description("행사 정보"),
                                fieldWithPath("eventInfo.content").type(STRING).description("행사 내용"),
                                fieldWithPath("eventInfo.activityArea").type(STRING).description("활동 지역"),
                                fieldWithPath("eventInfo.recruitmentTarget").type(STRING).description("모집 대상"),
                                fieldWithPath("eventInfo.recruitmentLimit").type(NUMBER).description("모집 인원"),
                                fieldWithPath("formInfo.openDate").type(STRING).description("폼 오픈 날짜"),
                                fieldWithPath("formInfo.openTime").type(STRING).description("폼 오픈 시간"),
                                fieldWithPath("formInfo.closeDate").type(STRING).description("폼 마감 날짜"),
                                fieldWithPath("formInfo.closeTime").type(STRING).description("폼 마감 시간")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("eventId").type(NUMBER).description("생성된 행사 id")
                        ))
                );
    }

    @Test
    @WithMockUser
    public void 클럽_일정_행사_생성에_성공한다() throws Exception {
        // given
        EventCreateRequest clubEventCreateRequest = EventCreateRequest.builder()
                .clubId(1L)
                .eventInfo(
                        EventCreateRequest.EventInfoRequest.builder()
                                .title("행사 제목")
                                .content("행사 내용")
                                .startDate(LocalDate.of(2023, 11, 15))
                                .startTime(LocalTime.of(14, 0))
                                .endDate(LocalDate.of(2023, 11, 16))
                                .endTime(LocalTime.of(18, 0))
                                .location("행사 장소")
                                .capacity(100)
                                .dues(5000)
                                .build()
                )
                .formInfo(
                        EventCreateRequest.FormInfoRequest.builder()
                                .openDate(LocalDate.of(2023, 11, 1))
                                .openTime(LocalTime.of(9, 0))
                                .closeDate(LocalDate.of(2023, 11, 10))
                                .closeTime(LocalTime.of(18, 0))
                                .build()
                )
                .build();

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(clubEventCreateRequest)
        );

        MockMultipartFile category = new MockMultipartFile(
                "category",
                "",
                TEXT_PLAIN_VALUE,
                CLUB.name().getBytes(StandardCharsets.UTF_8)
        );

        Club club = ClubTestFixture.club1();
        given(clubProvider.getClub(any(Long.class))).willReturn(club);
        given(eventService.create(any(EventCreateInfo.class), any(Club.class))).willReturn(1L);

        // when
        ResultActions actions = mvc.perform(multipart("/api/v1/events")
                .file(posterImage)
                .file(request)
                .file(category)
                .header(AUTHORIZATION, "Access Token")
                .with(csrf())
                .contentType(MULTIPART_FORM_DATA_VALUE)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document("event/create/club",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("posterImage").description("포스터 사진")
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE)).optional(),
                                partWithName("request").description("행사 생성 관련 정보")
                                        .attributes(key("content-type").value(APPLICATION_JSON_VALUE)),
                                partWithName("category").description("행사 카테고리")
                                        .attributes(key("content-type").value(TEXT_PLAIN_VALUE))
                        ),
                        requestPartFields("request",
                                fieldWithPath("clubId").type(NUMBER).description("클럽 id"),
                                fieldWithPath("eventInfo.title").type(STRING).description("행사 정보"),
                                fieldWithPath("eventInfo.content").type(STRING).description("행사 내용"),
                                fieldWithPath("eventInfo.startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("eventInfo.startTime").type(STRING).description("행사 시작 시간"),
                                fieldWithPath("eventInfo.endDate").type(STRING).description("행사 종료 날짜"),
                                fieldWithPath("eventInfo.endTime").type(STRING).description("행사 종료 시간"),
                                fieldWithPath("eventInfo.location").type(STRING).description("행사 장소"),
                                fieldWithPath("eventInfo.capacity").type(NUMBER).description("행사 정원"),
                                fieldWithPath("eventInfo.dues").type(NUMBER).description("행사 회비"),
                                fieldWithPath("formInfo.openDate").type(STRING).description("폼 오픈 날짜"),
                                fieldWithPath("formInfo.openTime").type(STRING).description("폼 오픈 시간"),
                                fieldWithPath("formInfo.closeDate").type(STRING).description("폼 마감 날짜"),
                                fieldWithPath("formInfo.closeTime").type(STRING).description("폼 마감 시간")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("eventId").type(NUMBER).description("생성된 행사 id")
                        ))
                );
    }

    @Test
    @WithMockUser
    public void 공연_행사_수정에_성공한다() throws Exception {
        // given
        EventUpdateRequest showEventUpdateRequest = EventUpdateRequest.builder()
                .eventId(1L)
                .eventInfo(
                        EventUpdateRequest.EventInfoRequest.builder()
                                .title("행사 제목")
                                .content("행사 내용")
                                .startDate(LocalDate.of(2023, 11, 15))
                                .startTime(LocalTime.of(14, 0))
                                .location("행사 장소")
                                .capacity(100)
                                .build()
                )
                .ticketInfo(
                        EventUpdateRequest.TicketInfoRequest.builder()
                                .cost(20000)
                                .maxTicketCount(2)
                                .build()
                )
                .formInfo(
                        EventUpdateRequest.FormInfoRequest.builder()
                                .openDate(LocalDate.of(2023, 11, 1))
                                .openTime(LocalTime.of(9, 0))
                                .closeDate(LocalDate.of(2023, 11, 10))
                                .closeTime(LocalTime.of(18, 0))
                                .build()
                )
                .bankInfo(
                        EventUpdateRequest.BankInfoRequest.builder()
                                .name("은행 명")
                                .accountNumber("은행 계좌번호")
                                .build()
                )
                .build();

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(showEventUpdateRequest)
        );

        MockMultipartFile category = new MockMultipartFile(
                "category",
                "",
                TEXT_PLAIN_VALUE,
                SHOW.name().getBytes(StandardCharsets.UTF_8)
        );

        doNothing().when(eventService).update(any(Event.class), any(Long.class), any(MultipartFile.class));

        // when
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/api/v1/events");
        requestBuilder.with(req -> {
            req.setMethod(HttpMethod.PATCH.name());
            return req;
        });

        ResultActions actions = mvc.perform(requestBuilder
                .file(posterImage)
                .file(request)
                .file(category)
                .header(AUTHORIZATION, "Access Token")
                .with(csrf())
                .contentType(MULTIPART_FORM_DATA_VALUE)
        );

        // then
        actions
                .andExpect(status().isNoContent())
                .andDo(document("event/update/show",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("posterImage").description("포스터 사진")
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE)).optional(),
                                partWithName("request").description("행사 생성 관련 정보")
                                        .attributes(key("content-type").value(APPLICATION_JSON_VALUE)),
                                partWithName("category").description("행사 카테고리")
                                        .attributes(key("content-type").value(TEXT_PLAIN_VALUE))
                        ),
                        requestPartFields("request",
                                fieldWithPath("eventId").type(NUMBER).description("행사 id"),
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
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        )
                ));
    }

    @Test
    @WithMockUser
    public void 홍보_행사_수정에_성공한다() throws Exception {
        // given
        EventUpdateRequest promotionEventCreateRequest = EventUpdateRequest.builder()
                .eventId(1L)
                .eventInfo(
                        EventUpdateRequest.EventInfoRequest.builder()
                                .title("행사 제목")
                                .content("행사 내용")
                                .startDate(LocalDate.of(2023, 11, 15))
                                .startTime(LocalTime.of(14, 0))
                                .location("행사 장소")
                                .capacity(100)
                                .build()
                )
                .formInfo(
                        EventUpdateRequest.FormInfoRequest.builder()
                                .openDate(LocalDate.of(2023, 11, 1))
                                .openTime(LocalTime.of(9, 0))
                                .closeDate(LocalDate.of(2023, 11, 10))
                                .closeTime(LocalTime.of(18, 0))
                                .build()
                )
                .build();

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(promotionEventCreateRequest)
        );

        MockMultipartFile category = new MockMultipartFile(
                "category",
                "",
                TEXT_PLAIN_VALUE,
                PROMOTION.name().getBytes(StandardCharsets.UTF_8)
        );

        doNothing().when(eventService).update(any(Event.class), any(Long.class), any(MultipartFile.class));

        // when
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/api/v1/events");
        requestBuilder.with(req -> {
            req.setMethod(HttpMethod.PATCH.name());
            return req;
        });

        ResultActions actions = mvc.perform(requestBuilder
                .file(posterImage)
                .file(request)
                .file(category)
                .header(AUTHORIZATION, "Access Token")
                .with(csrf())
                .contentType(MULTIPART_FORM_DATA_VALUE)
        );

        // then
        actions
                .andExpect(status().isNoContent())
                .andDo(document("event/update/promotion",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("posterImage").description("포스터 사진")
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE)).optional(),
                                partWithName("request").description("행사 생성 관련 정보")
                                        .attributes(key("content-type").value(APPLICATION_JSON_VALUE)),
                                partWithName("category").description("행사 카테고리")
                                        .attributes(key("content-type").value(TEXT_PLAIN_VALUE))
                        ),
                        requestPartFields("request",
                                fieldWithPath("eventId").type(NUMBER).description("행사 id"),
                                fieldWithPath("eventInfo.title").type(STRING).description("행사 정보"),
                                fieldWithPath("eventInfo.content").type(STRING).description("행사 내용"),
                                fieldWithPath("eventInfo.startDate").type(STRING).description("행사 날짜"),
                                fieldWithPath("eventInfo.startTime").type(STRING).description("행사 시간"),
                                fieldWithPath("eventInfo.location").type(STRING).description("행사 장소"),
                                fieldWithPath("eventInfo.capacity").type(NUMBER).description("행사 정원"),
                                fieldWithPath("formInfo.openDate").type(STRING).description("폼 오픈 날짜"),
                                fieldWithPath("formInfo.openTime").type(STRING).description("폼 오픈 시간"),
                                fieldWithPath("formInfo.closeDate").type(STRING).description("폼 마감 날짜"),
                                fieldWithPath("formInfo.closeTime").type(STRING).description("폼 마감 시간")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        )
                ));
    }

    @Test
    @WithMockUser
    public void 모집_공고_행사_수정에_성공한다() throws Exception {
        // given
        EventUpdateRequest recruitmentEventUpdateRequest = EventUpdateRequest.builder()
                .eventId(1L)
                .eventInfo(
                        EventUpdateRequest.EventInfoRequest.builder()
                                .title("행사 제목")
                                .content("행사 내용")
                                .activityArea("활동 지역")
                                .recruitmentTarget("모집 대상")
                                .recruitmentLimit(100)
                                .build()
                )
                .formInfo(
                        EventUpdateRequest.FormInfoRequest.builder()
                                .openDate(LocalDate.of(2023, 11, 1))
                                .openTime(LocalTime.of(9, 0))
                                .closeDate(LocalDate.of(2023, 11, 10))
                                .closeTime(LocalTime.of(18, 0))
                                .build()
                )
                .build();

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(recruitmentEventUpdateRequest)
        );

        MockMultipartFile category = new MockMultipartFile(
                "category",
                "",
                TEXT_PLAIN_VALUE,
                RECRUITMENT.name().getBytes(StandardCharsets.UTF_8)
        );

        doNothing().when(eventService).update(any(Event.class), any(Long.class), any(MultipartFile.class));

        // when
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/api/v1/events");
        requestBuilder.with(req -> {
            req.setMethod(HttpMethod.PATCH.name());
            return req;
        });

        ResultActions actions = mvc.perform(requestBuilder
                .file(posterImage)
                .file(request)
                .file(category)
                .header(AUTHORIZATION, "Access Token")
                .with(csrf())
                .contentType(MULTIPART_FORM_DATA_VALUE)
        );

        // then
        actions
                .andExpect(status().isNoContent())
                .andDo(document("event/update/recruitment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("posterImage").description("포스터 사진")
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE)).optional(),
                                partWithName("request").description("행사 생성 관련 정보")
                                        .attributes(key("content-type").value(APPLICATION_JSON_VALUE)),
                                partWithName("category").description("행사 카테고리")
                                        .attributes(key("content-type").value(TEXT_PLAIN_VALUE))
                        ),
                        requestPartFields("request",
                                fieldWithPath("eventId").type(NUMBER).description("행사 id"),
                                fieldWithPath("eventInfo.title").type(STRING).description("행사 정보"),
                                fieldWithPath("eventInfo.content").type(STRING).description("행사 내용"),
                                fieldWithPath("eventInfo.activityArea").type(STRING).description("활동 지역"),
                                fieldWithPath("eventInfo.recruitmentTarget").type(STRING).description("모집 대상"),
                                fieldWithPath("eventInfo.recruitmentLimit").type(NUMBER).description("모집 인원"),
                                fieldWithPath("formInfo.openDate").type(STRING).description("폼 오픈 날짜"),
                                fieldWithPath("formInfo.openTime").type(STRING).description("폼 오픈 시간"),
                                fieldWithPath("formInfo.closeDate").type(STRING).description("폼 마감 날짜"),
                                fieldWithPath("formInfo.closeTime").type(STRING).description("폼 마감 시간")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        )
                ));
    }

    @Test
    @WithMockUser
    public void 클럽_일정_행사_수정에_성공한다() throws Exception {
        // given
        EventUpdateRequest clubEventUpdateRequest = EventUpdateRequest.builder()
                .eventId(1L)
                .eventInfo(
                        EventUpdateRequest.EventInfoRequest.builder()
                                .title("행사 제목")
                                .content("행사 내용")
                                .startDate(LocalDate.of(2023, 11, 15))
                                .startTime(LocalTime.of(14, 0))
                                .endDate(LocalDate.of(2023, 11, 16))
                                .endTime(LocalTime.of(18, 0))
                                .location("행사 장소")
                                .capacity(100)
                                .dues(5000)
                                .build()
                )
                .formInfo(
                        EventUpdateRequest.FormInfoRequest.builder()
                                .openDate(LocalDate.of(2023, 11, 1))
                                .openTime(LocalTime.of(9, 0))
                                .closeDate(LocalDate.of(2023, 11, 10))
                                .closeTime(LocalTime.of(18, 0))
                                .build()
                )
                .build();

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(clubEventUpdateRequest)
        );

        MockMultipartFile category = new MockMultipartFile(
                "category",
                "",
                TEXT_PLAIN_VALUE,
                CLUB.name().getBytes(StandardCharsets.UTF_8)
        );

        doNothing().when(eventService).update(any(Event.class), any(Long.class), any(MultipartFile.class));
        // when
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/api/v1/events");
        requestBuilder.with(req -> {
            req.setMethod(HttpMethod.PATCH.name());
            return req;
        });

        ResultActions actions = mvc.perform(requestBuilder
                .file(posterImage)
                .file(request)
                .file(category)
                .header(AUTHORIZATION, "Access Token")
                .with(csrf())
                .contentType(MULTIPART_FORM_DATA_VALUE)
        );

        // then
        actions
                .andExpect(status().isNoContent())
                .andDo(document("event/update/club",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("posterImage").description("포스터 사진")
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE)).optional(),
                                partWithName("request").description("행사 생성 관련 정보")
                                        .attributes(key("content-type").value(APPLICATION_JSON_VALUE)),
                                partWithName("category").description("행사 카테고리")
                                        .attributes(key("content-type").value(TEXT_PLAIN_VALUE))
                        ),
                        requestPartFields("request",
                                fieldWithPath("eventId").type(NUMBER).description("행사 id"),
                                fieldWithPath("eventInfo.title").type(STRING).description("행사 정보"),
                                fieldWithPath("eventInfo.content").type(STRING).description("행사 내용"),
                                fieldWithPath("eventInfo.startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("eventInfo.startTime").type(STRING).description("행사 시작 시간"),
                                fieldWithPath("eventInfo.endDate").type(STRING).description("행사 종료 날짜"),
                                fieldWithPath("eventInfo.endTime").type(STRING).description("행사 종료 시간"),
                                fieldWithPath("eventInfo.location").type(STRING).description("행사 장소"),
                                fieldWithPath("eventInfo.capacity").type(NUMBER).description("행사 정원"),
                                fieldWithPath("eventInfo.dues").type(NUMBER).description("행사 회비"),
                                fieldWithPath("formInfo.openDate").type(STRING).description("폼 오픈 날짜"),
                                fieldWithPath("formInfo.openTime").type(STRING).description("폼 오픈 시간"),
                                fieldWithPath("formInfo.closeDate").type(STRING).description("폼 마감 날짜"),
                                fieldWithPath("formInfo.closeTime").type(STRING).description("폼 마감 시간")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        )
                ));
    }

    @Test
    @WithMockUser
    public void 전체_행사_조회에_성공한다() throws Exception {
        // given
        List<Event> events = List.of(event1(), showEvent(), clubEvent());
        Page<Event> eventPages = new PageImpl<>(events);

        given(eventService.getAll(any(EventCategory.class), any(Pageable.class))).willReturn(eventPages);

        // when
        ResultActions actions = mvc.perform(get("/api/v1/events")
                .param("category", "SHOW")
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
                                parameterWithName("category").description("행사 카테고리 (ex. SHOW, RECRUITMENT, PROMOTION, CLUB)"),
                                parameterWithName("page").optional().description("페이지"),
                                parameterWithName("size").optional().description("페이지 내 개수"),
                                parameterWithName("sort").optional().description("정렬 방법(ex. id,desc)")
                        ),
                        responseFields(
                                fieldWithPath("data").type(ARRAY).description("페이지 내 행사 정보"),
                                fieldWithPath("data[].id").type(NUMBER).description("행사 id"),
                                fieldWithPath("data[].eventInfo").type(OBJECT).description("행사 정보"),
                                fieldWithPath("data[].eventInfo.title").type(STRING).description("행사 제목"),
                                fieldWithPath("data[].eventInfo.posterImageUrl").type(STRING).description("포스터 URL"),
                                fieldWithPath("data[].eventInfo.location").type(STRING).description("행사 위치"),
                                fieldWithPath("data[].eventInfo.startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("data[].eventInfo.startTime").type(STRING).description("행사 시작 시간"),
                                fieldWithPath("data[].eventInfo.endDate").type(STRING).description("행사 종료 날짜"),
                                fieldWithPath("data[].eventInfo.endTime").type(STRING).description("행사 종료 시간"),
                                fieldWithPath("data[].eventInfo.isEnded").type(BOOLEAN).description("행사 종료 여부"),
                                fieldWithPath("data[].clubInfo").type(OBJECT).description("클럽 정보"),
                                fieldWithPath("data[].clubInfo.name").type(STRING).description("클럽 명"),
                                fieldWithPath("data[].clubInfo.logoImageUrl").type(STRING).description("클럽 이미지 Url"),
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
    void 행사_상세_조회에_성공한다_공연() throws Exception {
        // given
        Event event = showEvent();
        boolean hasAlreadyApplied = false;

        given(eventService.get(any(Long.class), any())).willReturn(event);
        given(userEventService.hasAlreadyApplied(any(Long.class), any())).willReturn(hasAlreadyApplied);

        // when
        ResultActions actions = mvc.perform(get("/api/v1/events/{eventId}", 1L));

        // then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("event/getShowEvent",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("eventId").description("행사 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("행사 ID"),
                                fieldWithPath("category").type(STRING).description("이벤트 종류"),
                                fieldWithPath("hasForm").type(BOOLEAN).description("폼 존재 여부"),
                                fieldWithPath("hasAlreadyApplied").type(BOOLEAN).description("폼 신청 여부"),
                                fieldWithPath("eventInfo").type(OBJECT).description("행사 정보"),
                                fieldWithPath("eventInfo.title").type(STRING).description("행사 제목"),
                                fieldWithPath("eventInfo.content").type(STRING).description("행사 내용"),
                                fieldWithPath("eventInfo.startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("eventInfo.startTime").type(STRING).description("행사 시작 시각"),
                                fieldWithPath("eventInfo.isEnded").type(BOOLEAN).description("행사 종료 여부"),
                                fieldWithPath("eventInfo.location").type(STRING).description("행사 위치"),
                                fieldWithPath("eventInfo.applicants").type(NUMBER).description("신청 인원"),
                                fieldWithPath("eventInfo.capacity").type(NUMBER).description("신청 정원"),
                                fieldWithPath("eventInfo.posterImageUrl").type(STRING).description("행사 포스터 URL"),
                                fieldWithPath("ticketInfo").type(OBJECT).description("티켓 정보"),
                                fieldWithPath("ticketInfo.cost").type(NUMBER).description("참가 비용"),
                                fieldWithPath("ticketInfo.maxTicketCount").type(NUMBER).description("인당 최대 예매 가능 수"),
                                fieldWithPath("formInfo").type(OBJECT).description("폼 정보"),
                                fieldWithPath("formInfo.openDate").type(STRING).description("행사 참여 신청 시작 날짜"),
                                fieldWithPath("formInfo.openTime").type(STRING).description("행사 참여 신청 시작 시간"),
                                fieldWithPath("formInfo.closeDate").type(STRING).description("행사 참여 신청 종료 날짜"),
                                fieldWithPath("formInfo.closeTime").type(STRING).description("행사 참여 신청 종료 시간"),
                                fieldWithPath("formInfo.isAbleToApply").type(BOOLEAN).description("참여 신청 가능 여부"),
                                fieldWithPath("bankInfo").type(OBJECT).description("은행 정보"),
                                fieldWithPath("bankInfo.bankName").type(STRING).description("은행 명"),
                                fieldWithPath("bankInfo.bankAccountNumber").type(STRING).description("계좌 번호"),
                                fieldWithPath("clubInfo.clubId").type(NUMBER).description("클럽 ID"),
                                fieldWithPath("clubInfo.clubName").type(STRING).description("클럽 이름")
                        )));
    }

    @Test
    @WithMockUser
    void 행사_상세_조회에_성공한다_홍보() throws Exception {
        // given
        Event event = promotionEvent();
        boolean hasAlreadyApplied = false;

        given(eventService.get(any(Long.class), any())).willReturn(event);
        given(userEventService.hasAlreadyApplied(any(Long.class), any())).willReturn(hasAlreadyApplied);

        // when
        ResultActions actions = mvc.perform(get("/api/v1/events/{eventId}", 1L));

        // then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("event/getPromotionEvent",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("eventId").description("행사 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("행사 ID"),
                                fieldWithPath("category").type(STRING).description("이벤트 종류"),
                                fieldWithPath("hasForm").type(BOOLEAN).description("폼 존재 여부"),
                                fieldWithPath("hasAlreadyApplied").type(BOOLEAN).description("폼 신청 여부"),
                                fieldWithPath("eventInfo").type(OBJECT).description("행사 정보"),
                                fieldWithPath("eventInfo.title").type(STRING).description("행사 제목"),
                                fieldWithPath("eventInfo.content").type(STRING).description("행사 내용"),
                                fieldWithPath("eventInfo.startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("eventInfo.startTime").type(STRING).description("행사 시작 시각"),
                                fieldWithPath("eventInfo.isEnded").type(BOOLEAN).description("행사 종료 여부"),
                                fieldWithPath("eventInfo.location").type(STRING).description("행사 위치"),
                                fieldWithPath("eventInfo.applicants").type(NUMBER).description("신청 인원"),
                                fieldWithPath("eventInfo.capacity").type(NUMBER).description("신청 정원"),
                                fieldWithPath("eventInfo.posterImageUrl").type(STRING).description("행사 포스터 URL"),
                                fieldWithPath("formInfo").type(OBJECT).description("폼 정보"),
                                fieldWithPath("formInfo.openDate").type(STRING).description("행사 참여 신청 시작 날짜"),
                                fieldWithPath("formInfo.openTime").type(STRING).description("행사 참여 신청 시작 시간"),
                                fieldWithPath("formInfo.closeDate").type(STRING).description("행사 참여 신청 종료 날짜"),
                                fieldWithPath("formInfo.closeTime").type(STRING).description("행사 참여 신청 종료 시간"),
                                fieldWithPath("formInfo.isAbleToApply").type(BOOLEAN).description("참여 신청 가능 여부"),
                                fieldWithPath("clubInfo.clubId").type(NUMBER).description("클럽 ID"),
                                fieldWithPath("clubInfo.clubName").type(STRING).description("클럽 이름")
                        )));
    }

    @Test
    @WithMockUser
    void 행사_상세_조회에_성공한다_모집_공고() throws Exception {
        // given
        Event event = recruitmentEvent();
        boolean hasAlreadyApplied = false;

        given(eventService.get(any(Long.class), any())).willReturn(event);
        given(userEventService.hasAlreadyApplied(any(Long.class), any())).willReturn(hasAlreadyApplied);

        // when
        ResultActions actions = mvc.perform(get("/api/v1/events/{eventId}", 1L));

        // then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("event/getRecruitmentEvent",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("eventId").description("행사 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("행사 ID"),
                                fieldWithPath("category").type(STRING).description("이벤트 종류"),
                                fieldWithPath("hasForm").type(BOOLEAN).description("폼 존재 여부"),
                                fieldWithPath("hasAlreadyApplied").type(BOOLEAN).description("폼 신청 여부"),
                                fieldWithPath("eventInfo").type(OBJECT).description("행사 정보"),
                                fieldWithPath("eventInfo.title").type(STRING).description("행사 제목"),
                                fieldWithPath("eventInfo.content").type(STRING).description("행사 내용"),
                                fieldWithPath("eventInfo.startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("eventInfo.startTime").type(STRING).description("행사 시작 시각"),
                                fieldWithPath("eventInfo.isEnded").type(BOOLEAN).description("행사 종료 여부"),
                                fieldWithPath("eventInfo.applicants").type(NUMBER).description("신청 인원"),
                                fieldWithPath("eventInfo.posterImageUrl").type(STRING).description("행사 포스터 URL"),
                                fieldWithPath("eventInfo.activityArea").type(STRING).description("활동 영역"),
                                fieldWithPath("eventInfo.recruitmentTarget").type(STRING).description("모집 대상"),
                                fieldWithPath("eventInfo.recruitmentLimit").type(NUMBER).description("모집 인원"),
                                fieldWithPath("formInfo").type(OBJECT).description("폼 정보"),
                                fieldWithPath("formInfo.openDate").type(STRING).description("행사 참여 신청 시작 날짜"),
                                fieldWithPath("formInfo.openTime").type(STRING).description("행사 참여 신청 시작 시간"),
                                fieldWithPath("formInfo.closeDate").type(STRING).description("행사 참여 신청 종료 날짜"),
                                fieldWithPath("formInfo.closeTime").type(STRING).description("행사 참여 신청 종료 시간"),
                                fieldWithPath("formInfo.isAbleToApply").type(BOOLEAN).description("참여 신청 가능 여부"),
                                fieldWithPath("clubInfo.clubId").type(NUMBER).description("클럽 ID"),
                                fieldWithPath("clubInfo.clubName").type(STRING).description("클럽 이름")
                        )));
    }

    @Test
    @WithMockUser
    void 행사_상세_조회에_성공한다_클럽_일정() throws Exception {
        // given
        Event event = clubEvent();
        boolean hasAlreadyApplied = false;

        given(eventService.get(any(Long.class), any())).willReturn(event);
        given(userEventService.hasAlreadyApplied(any(Long.class), any())).willReturn(hasAlreadyApplied);


        // when
        ResultActions actions = mvc.perform(get("/api/v1/events/{eventId}", 1L)
                .header(AUTHORIZATION, "Access Token"));

        // then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("event/getClubEvent",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("eventId").description("행사 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("행사 ID"),
                                fieldWithPath("category").type(STRING).description("이벤트 종류"),
                                fieldWithPath("hasForm").type(BOOLEAN).description("폼 존재 여부"),
                                fieldWithPath("hasAlreadyApplied").type(BOOLEAN).description("폼 신청 여부"),
                                fieldWithPath("eventInfo").type(OBJECT).description("행사 정보"),
                                fieldWithPath("eventInfo.title").type(STRING).description("행사 제목"),
                                fieldWithPath("eventInfo.content").type(STRING).description("행사 제목"),
                                fieldWithPath("eventInfo.startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("eventInfo.startTime").type(STRING).description("행사 시작 시각"),
                                fieldWithPath("eventInfo.endDate").type(STRING).description("행사 종료 날짜"),
                                fieldWithPath("eventInfo.endTime").type(STRING).description("행사 종료 시각"),
                                fieldWithPath("eventInfo.isEnded").type(BOOLEAN).description("행사 종료 여부"),
                                fieldWithPath("eventInfo.dues").type(NUMBER).description("행사 참가 회비"),
                                fieldWithPath("eventInfo.location").type(STRING).description("행사 위치"),
                                fieldWithPath("eventInfo.applicants").type(NUMBER).description("신청 인원"),
                                fieldWithPath("eventInfo.capacity").type(NUMBER).description("모집 정원"),
                                fieldWithPath("eventInfo.posterImageUrl").type(STRING).description("행사 포스터 URL"),
                                fieldWithPath("formInfo").type(OBJECT).description("폼 정보"),
                                fieldWithPath("formInfo.openDate").type(STRING).description("행사 참여 신청 시작 날짜"),
                                fieldWithPath("formInfo.openTime").type(STRING).description("행사 참여 신청 시작 시간"),
                                fieldWithPath("formInfo.closeDate").type(STRING).description("행사 참여 신청 종료 날짜"),
                                fieldWithPath("formInfo.closeTime").type(STRING).description("행사 참여 신청 종료 시간"),
                                fieldWithPath("formInfo.isAbleToApply").type(BOOLEAN).description("참여 신청 가능 여부"),
                                fieldWithPath("clubInfo.clubId").type(NUMBER).description("클럽 ID"),
                                fieldWithPath("clubInfo.clubName").type(STRING).description("클럽 이름")
                        )));
    }

    @Test
    @WithMockUser
    void 행사_검색에_성공한다() throws Exception { // 없어도 됨. 있어도 안씀.
        // given
        List<Event> events = List.of(event1(), showEvent(), clubEvent());
        Page<Event> eventPages = new PageImpl<>(events);

        given(eventService.search(any(String.class), any(Pageable.class))).willReturn(eventPages);

        // when
        ResultActions actions = mvc.perform(get("/api/v1/events/searches")
                .param("keyword", "title")
                .param("page", "1")
                .param("size", "3")
                .param("sort", "id,desc")
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
                .andDo(document("event/search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("keyword").description("검색어"),
                                parameterWithName("page").optional().description("페이지"),
                                parameterWithName("size").optional().description("페이지 내 개수"),
                                parameterWithName("sort").optional().description("정렬 방법(ex. id,desc)")
                        ),
                        responseFields(
                                fieldWithPath("data").type(ARRAY).description("페이지 내 행사 정보"),
                                fieldWithPath("data[].id").type(NUMBER).description("행사 id"),
                                fieldWithPath("data[].eventInfo").type(OBJECT).description("행사 정보"),
                                fieldWithPath("data[].eventInfo.title").type(STRING).description("행사 제목"),
                                fieldWithPath("data[].eventInfo.posterImageUrl").type(STRING).description("포스터 URL"),
                                fieldWithPath("data[].eventInfo.location").type(STRING).description("행사 위치"),
                                fieldWithPath("data[].eventInfo.startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("data[].eventInfo.startTime").type(STRING).description("행사 시작 시간"),
                                fieldWithPath("data[].eventInfo.endDate").type(STRING).description("행사 종료 날짜"),
                                fieldWithPath("data[].eventInfo.endTime").type(STRING).description("행사 종료 시간"),
                                fieldWithPath("data[].eventInfo.isEnded").type(BOOLEAN).description("행사 종료 여부"),
                                fieldWithPath("data[].clubInfo").type(OBJECT).description("클럽 정보"),
                                fieldWithPath("data[].clubInfo.name").type(STRING).description("클럽 명"),
                                fieldWithPath("data[].clubInfo.logoImageUrl").type(STRING).description("클럽 이미지 Url"),
                                fieldWithPath("pageData").type(OBJECT).description("페이지 정보"),
                                fieldWithPath("pageData.first").type(BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("pageData.last").type(BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("pageData.pageNumber").type(NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("pageData.size").type(NUMBER).description("페이지 내 개수"),
                                fieldWithPath("pageData.totalPages").type(NUMBER).description("총 페이지 개수"),
                                fieldWithPath("pageData.totalElements").type(NUMBER).description("총 행사 개수")
                        ))
                );
    }

    @Test
    @WithMockUser
    void 행사_삭제에_성공한다() throws Exception {
        // given
        doNothing().when(eventService).delete(any(Long.class), any());

        // when
        ResultActions actions = mvc.perform(delete("/api/v1/events/{eventId}", 1L)
                .header(AUTHORIZATION, "Access Token")
                .with(csrf())
        );

        // then
        actions
                .andExpect(status().isNoContent())
                .andDo(document("event/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("eventId").description("행사 ID")
                        ))
                );
    }

    @Test
    @WithMockUser
    void 배너_행사_조회에_성공한다() throws Exception {
        // given
        List<Event> events = List.of(event1(), showEvent(), clubEvent());
        given(eventService.getBanner(any(LocalDateTime.class), any(Integer.class))).willReturn(events);

        // when, then
        mvc.perform(get("/api/v1/events/banner")
                        .with(csrf())
                ).andExpect(status().isOk())
                .andDo(print())
                .andDo(document("event/banner",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("[]").type(ARRAY).description("클럽 및 이벤트 정보 목록"),
                                        fieldWithPath("[].clubInfo").type(OBJECT).description("클럽 정보"),
                                        fieldWithPath("[].clubInfo.coverImageUrl").type(STRING).description("클럽 이미지 url"),
                                        fieldWithPath("[].clubInfo.name").type(STRING).description("클럽 이름"),
                                        fieldWithPath("[].eventInfo").type(OBJECT).description("이벤트 정보"),
                                        fieldWithPath("[].eventInfo.eventId").type(NUMBER).description("행사 id"),
                                        fieldWithPath("[].eventInfo.title").type(STRING).description("행사 제목"),
                                        fieldWithPath("[].eventInfo.formCloseDateTime").type(STRING).description("폼 마감 시간"),
                                        fieldWithPath("[].eventInfo.eventCategory").type(STRING).description("행사 카테고리")
                                )
                        )
                );
    }

}
