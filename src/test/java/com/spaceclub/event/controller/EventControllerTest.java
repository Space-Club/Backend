package com.spaceclub.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.event.controller.dto.EventApplicationCreateRequest;
import com.spaceclub.event.controller.dto.createRequest.ClubEventCreateRequest;
import com.spaceclub.event.controller.dto.createRequest.PromotionEventCreateRequest;
import com.spaceclub.event.controller.dto.createRequest.RecruitmentEventCreateRequest;
import com.spaceclub.event.controller.dto.createRequest.ShowEventCreateRequest;
import com.spaceclub.event.controller.dto.updateRequest.ClubEventUpdateRequest;
import com.spaceclub.event.controller.dto.updateRequest.PromotionEventUpdateRequest;
import com.spaceclub.event.controller.dto.updateRequest.RecruitmentEventUpdateRequest;
import com.spaceclub.event.controller.dto.updateRequest.ShowEventUpdateRequest;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.service.EventService;
import com.spaceclub.event.service.vo.EventApplicationCreateInfo;
import com.spaceclub.form.FormTestFixture;
import com.spaceclub.global.S3ImageUploader;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.spaceclub.event.EventTestFixture.clubEvent;
import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.promotionEvent;
import static com.spaceclub.event.EventTestFixture.recruitmentEvent;
import static com.spaceclub.event.EventTestFixture.showEvent;
import static com.spaceclub.event.domain.ApplicationStatus.CANCELED;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = EventController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        JwtAuthorizationInterceptor.class,
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
    private S3ImageUploader uploader;

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
        ShowEventCreateRequest showEventCreateRequest = new ShowEventCreateRequest(
                1L,
                new ShowEventCreateRequest.EventInfoRequest(
                        "행사 제목",
                        "행사 내용",
                        LocalDate.of(2023, 11, 15),
                        LocalTime.of(14, 0),
                        "행사 장소",
                        100
                ),
                new ShowEventCreateRequest.TicketInfoRequest(20000, 2),
                new ShowEventCreateRequest.BankInfoRequest("은행 명", "은행 계좌번호"),
                new ShowEventCreateRequest.FormInfoRequest(
                        LocalDate.of(2023, 11, 1),
                        LocalTime.of(9, 0),
                        LocalDate.of(2023, 11, 10),
                        LocalTime.of(18, 0)
                )
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

        final String posterImageUrl = "image.jpeg";
        given(uploader.uploadPosterImage(any(MultipartFile.class))).willReturn(posterImageUrl);
        given(eventService.create(any(Event.class), any(Long.class), any(Long.class))).willReturn(1L);

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
        PromotionEventCreateRequest promotionEventCreateRequest = new PromotionEventCreateRequest(
                1L,
                new PromotionEventCreateRequest.EventInfoRequest(
                        "행사 제목",
                        "행사 내용",
                        LocalDate.of(2023, 11, 15),
                        LocalTime.of(14, 0),
                        "행사 장소",
                        100
                ),
                new PromotionEventCreateRequest.FormInfoRequest(
                        LocalDate.of(2023, 11, 1),
                        LocalTime.of(9, 0),
                        LocalDate.of(2023, 11, 10),
                        LocalTime.of(18, 0)
                )
        );

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

        final String posterImageUrl = "image.jpeg";
        given(uploader.uploadPosterImage(any(MultipartFile.class))).willReturn(posterImageUrl);
        given(eventService.create(any(Event.class), any(Long.class), any(Long.class))).willReturn(1L);

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
        RecruitmentEventCreateRequest recruitmentEventCreateRequest = new RecruitmentEventCreateRequest(
                1L,
                new RecruitmentEventCreateRequest.EventInfoRequest(
                        "행사 제목",
                        "행사 내용",
                        "활동 지역",
                        "모집 대상",
                        100
                ),
                new RecruitmentEventCreateRequest.FormInfoRequest(
                        LocalDate.of(2023, 11, 1),
                        LocalTime.of(9, 0),
                        LocalDate.of(2023, 11, 10),
                        LocalTime.of(18, 0)
                )
        );

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

        final String posterImageUrl = "image.jpeg";
        given(uploader.uploadPosterImage(any(MultipartFile.class))).willReturn(posterImageUrl);
        given(eventService.create(any(Event.class), any(Long.class), any(Long.class))).willReturn(1L);

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
    public void 클럽_일정_행사_생성에_성공한다() throws Exception {
        // given
        ClubEventCreateRequest clubEventCreateRequest = new ClubEventCreateRequest(
                1L,
                new ClubEventCreateRequest.EventInfoRequest(
                        "행사 제목",
                        "행사 내용",
                        LocalDate.of(2023, 11, 15),
                        LocalTime.of(14, 0),
                        LocalDate.of(2023, 11, 16),
                        LocalTime.of(18, 0),
                        "행사 장소",
                        100,
                        5000,
                        "담당자 이름"
                ),
                new ClubEventCreateRequest.FormInfoRequest(
                        LocalDate.of(2023, 11, 1),
                        LocalTime.of(9, 0),
                        LocalDate.of(2023, 11, 10),
                        LocalTime.of(18, 0)
                )
        );

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

        final String posterImageUrl = "image.jpeg";
        given(uploader.uploadPosterImage(any(MultipartFile.class))).willReturn(posterImageUrl);
        given(eventService.create(any(Event.class), any(Long.class), any(Long.class))).willReturn(1L);

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
                                fieldWithPath("eventInfo.startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("eventInfo.startTime").type(STRING).description("행사 시작 시간"),
                                fieldWithPath("eventInfo.endDate").type(STRING).description("행사 종료 날짜"),
                                fieldWithPath("eventInfo.endTime").type(STRING).description("행사 종료 시간"),
                                fieldWithPath("eventInfo.location").type(STRING).description("행사 장소"),
                                fieldWithPath("eventInfo.capacity").type(NUMBER).description("행사 정원"),
                                fieldWithPath("eventInfo.dues").type(NUMBER).description("행사 회비"),
                                fieldWithPath("eventInfo.managerName").type(STRING).description("담당자 이름"),
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
        ShowEventUpdateRequest showEventUpdateRequest = new ShowEventUpdateRequest(
                1L,
                new ShowEventUpdateRequest.EventInfoRequest(
                        "행사 제목",
                        "행사 내용",
                        LocalDate.of(2023, 11, 15),
                        LocalTime.of(14, 0),
                        "행사 장소",
                        100
                ),
                new ShowEventUpdateRequest.TicketInfoRequest(20000, 2),
                new ShowEventUpdateRequest.BankInfoRequest("은행 명", "은행 계좌번호"),
                new ShowEventUpdateRequest.FormInfoRequest(
                        LocalDate.of(2023, 11, 1),
                        LocalTime.of(9, 0),
                        LocalDate.of(2023, 11, 10),
                        LocalTime.of(18, 0)
                )
        );
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

        final String posterImageUrl = "image.jpeg";
        given(uploader.uploadPosterImage(any(MultipartFile.class))).willReturn(posterImageUrl);
        given(eventService.create(any(Event.class), any(Long.class), any(Long.class))).willReturn(1L);

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
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE)),
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
        PromotionEventUpdateRequest promotionEventCreateRequest = new PromotionEventUpdateRequest(
                1L,
                new PromotionEventUpdateRequest.EventInfoRequest(
                        "행사 제목",
                        "행사 내용",
                        LocalDate.of(2023, 11, 15),
                        LocalTime.of(14, 0),
                        "행사 장소",
                        100
                ),
                new PromotionEventUpdateRequest.FormInfoRequest(
                        LocalDate.of(2023, 11, 1),
                        LocalTime.of(9, 0),
                        LocalDate.of(2023, 11, 10),
                        LocalTime.of(18, 0)
                )
        );

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

        final String posterImageUrl = "image.jpeg";
        given(uploader.uploadPosterImage(any(MultipartFile.class))).willReturn(posterImageUrl);
        given(eventService.create(any(Event.class), any(Long.class), any(Long.class))).willReturn(1L);

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
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE)),
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
        RecruitmentEventUpdateRequest recruitmentEventUpdateRequest = new RecruitmentEventUpdateRequest(
                1L,
                new RecruitmentEventUpdateRequest.EventInfoRequest(
                        "행사 제목",
                        "행사 내용",
                        "활동 지역",
                        "모집 대상",
                        100
                ),
                new RecruitmentEventUpdateRequest.FormInfoRequest(
                        LocalDate.of(2023, 11, 1),
                        LocalTime.of(9, 0),
                        LocalDate.of(2023, 11, 10),
                        LocalTime.of(18, 0)
                )
        );

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

        final String posterImageUrl = "image.jpeg";
        given(uploader.uploadPosterImage(any(MultipartFile.class))).willReturn(posterImageUrl);
        given(eventService.create(any(Event.class), any(Long.class), any(Long.class))).willReturn(1L);

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
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE)),
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
    public void 클럽_일정_행사_수정에_성공한다() throws Exception {
        // given
        ClubEventUpdateRequest clubEventUpdateRequest = new ClubEventUpdateRequest(
                1L,
                new ClubEventUpdateRequest.EventInfoRequest(
                        "행사 제목",
                        "행사 내용",
                        LocalDate.of(2023, 11, 15),
                        LocalTime.of(14, 0),
                        LocalDate.of(2023, 11, 16),
                        LocalTime.of(18, 0),
                        "행사 장소",
                        100,
                        5000,
                        "담당자 이름"
                ),
                new ClubEventUpdateRequest.FormInfoRequest(
                        LocalDate.of(2023, 11, 1),
                        LocalTime.of(9, 0),
                        LocalDate.of(2023, 11, 10),
                        LocalTime.of(18, 0)
                )
        );

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

        final String posterImageUrl = "image.jpeg";
        given(uploader.uploadPosterImage(any(MultipartFile.class))).willReturn(posterImageUrl);
        given(eventService.create(any(Event.class), any(Long.class), any(Long.class))).willReturn(1L);

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
                                        .attributes(key("content-type").value(IMAGE_JPEG_VALUE)),
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
                                fieldWithPath("eventInfo.managerName").type(STRING).description("담당자 이름"),
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

        given(eventService.getAllEvents(any(EventCategory.class), any(Pageable.class))).willReturn(eventPages);

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
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("페이지 내 개수"),
                                parameterWithName("sort").description("정렬 방법(ex. id,desc)"),
                                parameterWithName("category").description("행사 카테고리 (ex. SHOW, RECRUITMENT, PROMOTION, CLUB)")
                        ),
                        responseFields(
                                fieldWithPath("data").type(ARRAY).description("페이지 내 행사 정보"),
                                fieldWithPath("data[].id").type(NUMBER).description("행사 아이디"),
                                fieldWithPath("data[].title").type(STRING).description("행사 제목"),
                                fieldWithPath("data[].posterImageUrl").type(STRING).description("포스터 URL"),
                                fieldWithPath("data[].location").type(STRING).description("행사 위치"),
                                fieldWithPath("data[].startDate").type(STRING).description("행사 날짜"),
                                fieldWithPath("data[].startTime").type(STRING).description("행사 시간"),
                                fieldWithPath("data[].formCloseDate").type(STRING).description("폼 마감 날짜"),
                                fieldWithPath("data[].formCloseTime").type(STRING).description("폼 마감 시간"),
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
    void 행사_상세_조회에_성공한다_공연() throws Exception {
        // given
        Event event = event1();

        given(eventService.get(any(Long.class))).willReturn(event);

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
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("행사 ID"),
                                fieldWithPath("title").type(STRING).description("행사 제목"),
                                fieldWithPath("content").type(STRING).description("행사 내용"),
                                fieldWithPath("cost").type(NUMBER).description("참가 비용"),
                                fieldWithPath("posterImageUrl").type(STRING).description("행사 포스터 URL"),
                                fieldWithPath("startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("startTime").type(STRING).description("행사 시작 시각"),
                                fieldWithPath("endDate").type(STRING).description("행사 종료 날짜"),
                                fieldWithPath("endTime").type(STRING).description("행사 종료 시각"),
                                fieldWithPath("location").type(STRING).description("행사 위치"),
                                fieldWithPath("clubName").type(STRING).description("행사 주최 클럽 이름"),
                                fieldWithPath("clubLogoImageUrl").type(STRING).description("행사 주최 클럽 로고 이미지 Url"),
                                fieldWithPath("formOpenDate").type(STRING).description("행사 참여 신청 시작 날짜"),
                                fieldWithPath("formOpenTime").type(STRING).description("행사 참여 신청 시작 시간"),
                                fieldWithPath("formCloseDate").type(STRING).description("행사 참여 신청 종료 날짜"),
                                fieldWithPath("formCloseTime").type(STRING).description("행사 참여 신청 종료 시간"),
                                fieldWithPath("isBookmarked").type(BOOLEAN).description("북마크 여부"),
                                fieldWithPath("applicants").type(NUMBER).description("신청자 수"),
                                fieldWithPath("capacity").type(NUMBER).description("신청 정원"),
                                fieldWithPath("eventCategory").type(STRING).description("이벤트 종류"),
                                fieldWithPath("isManager").type(BOOLEAN).description("매니저 여부"),
                                fieldWithPath("hasForm").type(BOOLEAN).description("폼 존재 여부"),
                                fieldWithPath("maxTicketCount").type(NUMBER).description("인당 최대 예매 가능 수")
                        )));
    }

    @Test
    @WithMockUser
    void 행사_상세_조회에_성공한다_홍보() throws Exception {
        // given
        Event event = promotionEvent();

        given(eventService.get(any(Long.class))).willReturn(event);

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
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("행사 ID"),
                                fieldWithPath("title").type(STRING).description("행사 제목"),
                                fieldWithPath("content").type(STRING).description("행사 내용"),
                                fieldWithPath("posterImageUrl").type(STRING).description("행사 포스터 URL"),
                                fieldWithPath("startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("startTime").type(STRING).description("행사 시작 시각"),
                                fieldWithPath("endDate").type(STRING).description("행사 종료 날짜"),
                                fieldWithPath("endTime").type(STRING).description("행사 종료 시각"),
                                fieldWithPath("activityArea").type(STRING).description("활동 영역"),
                                fieldWithPath("formOpenDate").type(STRING).description("행사 참여 신청 시작 날짜"),
                                fieldWithPath("formOpenTime").type(STRING).description("행사 참여 신청 시작 시간"),
                                fieldWithPath("formCloseDate").type(STRING).description("행사 참여 신청 종료 날짜"),
                                fieldWithPath("formCloseTime").type(STRING).description("행사 참여 신청 종료 시간"),
                                fieldWithPath("clubName").type(STRING).description("행사 주최 클럽 이름"),
                                fieldWithPath("clubLogoImageUrl").type(STRING).description("행사 주최 클럽 로고 이미지 Url"),
                                fieldWithPath("isBookmarked").type(BOOLEAN).description("북마크 여부"),
                                fieldWithPath("applicants").type(NUMBER).description("신청자 수"),
                                fieldWithPath("capacity").type(NUMBER).description("신청 정원"),
                                fieldWithPath("eventCategory").type(STRING).description("이벤트 종류"),
                                fieldWithPath("isManager").type(BOOLEAN).description("이벤트 종류"),
                                fieldWithPath("hasForm").type(BOOLEAN).description("폼 존재 여부")
                        )));
    }

    @Test
    @WithMockUser
    void 행사_상세_조회에_성공한다_모집_공고() throws Exception {
        // given
        Event event = recruitmentEvent();

        given(eventService.get(any(Long.class))).willReturn(event);

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
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("행사 ID"),
                                fieldWithPath("title").type(STRING).description("행사 제목"),
                                fieldWithPath("content").type(STRING).description("행사 내용"),
                                fieldWithPath("posterImageUrl").type(STRING).description("행사 포스터 URL"),
                                fieldWithPath("startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("startTime").type(STRING).description("행사 시작 시각"),
                                fieldWithPath("endDate").type(STRING).description("행사 종료 날짜"),
                                fieldWithPath("endTime").type(STRING).description("행사 종료 시각"),
                                fieldWithPath("location").type(STRING).description("행사 위치"),
                                fieldWithPath("recruitmentTarget").type(STRING).description("모집 대상"),
                                fieldWithPath("applicants").type(NUMBER).description("신청 인원"),
                                fieldWithPath("capacity").type(NUMBER).description("모집 정원"),
                                fieldWithPath("clubName").type(STRING).description("행사 주최 클럽 이름"),
                                fieldWithPath("clubLogoImageUrl").type(STRING).description("행사 주최 클럽 로고 이미지 Url"),
                                fieldWithPath("formOpenDate").type(STRING).description("행사 참여 신청 시작 날짜"),
                                fieldWithPath("formOpenTime").type(STRING).description("행사 참여 신청 시작 시간"),
                                fieldWithPath("formCloseDate").type(STRING).description("행사 참여 신청 종료 날짜"),
                                fieldWithPath("formCloseTime").type(STRING).description("행사 참여 신청 종료 시간"),
                                fieldWithPath("isBookmarked").type(BOOLEAN).description("북마크 여부"),
                                fieldWithPath("eventCategory").type(STRING).description("이벤트 종류"),
                                fieldWithPath("isManager").type(BOOLEAN).description("매니저 여부"),
                                fieldWithPath("hasForm").type(BOOLEAN).description("폼 존재 여부")
                        )));
    }

    @Test
    @WithMockUser
    void 행사_상세_조회에_성공한다_클럽_일정() throws Exception {
        // given
        Event event = clubEvent();

        given(eventService.get(any(Long.class))).willReturn(event);

        // when
        ResultActions actions = mvc.perform(get("/api/v1/events/{eventId}", 1L));

        // then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("event/getClubEvent",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("eventId").description("행사 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("행사 ID"),
                                fieldWithPath("title").type(STRING).description("행사 제목"),
                                fieldWithPath("content").type(STRING).description("행사 제목"),
                                fieldWithPath("posterImageUrl").type(STRING).description("행사 포스터 URL"),
                                fieldWithPath("startDate").type(STRING).description("행사 시작 날짜"),
                                fieldWithPath("startTime").type(STRING).description("행사 시작 시각"),
                                fieldWithPath("endDate").type(STRING).description("행사 종료 날짜"),
                                fieldWithPath("endTime").type(STRING).description("행사 종료 시각"),
                                fieldWithPath("location").type(STRING).description("행사 위치"),
                                fieldWithPath("dues").type(NUMBER).description("행사 참가 회비"),
                                fieldWithPath("applicants").type(NUMBER).description("신청 인원"),
                                fieldWithPath("capacity").type(NUMBER).description("모집 정원"),
                                fieldWithPath("clubName").type(STRING).description("행사 주최 클럽 이름"),
                                fieldWithPath("clubLogoImageUrl").type(STRING).description("행사 주최 클럽 로고 이미지 Url"),
                                fieldWithPath("formOpenDate").type(STRING).description("행사 참여 신청 시작 날짜"),
                                fieldWithPath("formOpenTime").type(STRING).description("행사 참여 신청 시작 시간"),
                                fieldWithPath("formCloseDate").type(STRING).description("행사 참여 신청 종료 날짜"),
                                fieldWithPath("formCloseTime").type(STRING).description("행사 참여 신청 종료 시간"),
                                fieldWithPath("isBookmarked").type(BOOLEAN).description("북마크 여부"),
                                fieldWithPath("eventCategory").type(STRING).description("이벤트 종류"),
                                fieldWithPath("isManager").type(BOOLEAN).description("매니저 여부"),
                                fieldWithPath("hasForm").type(BOOLEAN).description("폼 존재 여부")
                        )));
    }

    @Test
    @WithMockUser
    void 행사_참여_신청_취소에_성공한다() throws Exception {
        // given
        given(eventService.cancelEvent(any(Long.class), any())).willReturn(CANCELED);

        // when
        ResultActions actions = mvc.perform(delete("/api/v1/events/{eventId}/applications", 1L)
                .header(AUTHORIZATION, "Access Token")
                .with(csrf())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("event/cancel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("eventId").description("행사 id")
                        ),
                        responseFields(
                                fieldWithPath("applicationStatus").type(STRING).description("행사 신청 상태(ex. CANCELED, CANCEL_REQUESTED)")
                        )
                ));
    }

    @Test
    @WithMockUser
    public void 행사_검색에_성공한다() throws Exception {
        // given
        List<Event> events = List.of(event1(), showEvent(), clubEvent());
        Page<Event> eventPages = new PageImpl<>(events);

        given(eventService.getSearchEvents(any(String.class), any(Pageable.class), any())).willReturn(eventPages);

        // when
        ResultActions actions = mvc.perform(get("/api/v1/events/searches")
                .header(AUTHORIZATION, "Access Token")
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
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        queryParameters(
                                parameterWithName("keyword").description("검색어"),
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("페이지 내 개수"),
                                parameterWithName("sort").description("정렬 방법((ex) id,desc)")
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
                                fieldWithPath("data[].formInfo").type(OBJECT).description("폼 정보"),
                                fieldWithPath("data[].formInfo.startDate").type(STRING).description("폼 시작 날짜"),
                                fieldWithPath("data[].formInfo.startTime").type(STRING).description("폼 시작 시간"),
                                fieldWithPath("data[].formInfo.endDate").type(STRING).description("폼 종료 날짜"),
                                fieldWithPath("data[].formInfo.endTime").type(STRING).description("폼 종료 시간"),
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
    public void 행사_삭제에_성공한다() throws Exception {
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
    void 행사_신청에_성공한다() throws Exception {
        // given
        EventApplicationCreateRequest request = EventApplicationCreateRequest.builder()
                .eventId(1L)
                .ticketCount(5)
                .forms(List.of(
                        new EventApplicationCreateRequest.FormRequest(FormTestFixture.formOption1().getId(), "박씨"),
                        new EventApplicationCreateRequest.FormRequest(FormTestFixture.formOption2().getId(), "010-1111-2222")
                ))
                .build();

        Long userId = 1L;
        doNothing().when(eventService).createApplicationForm(EventApplicationCreateInfo.builder()
                .userId(userId)
                .eventId(request.eventId())
                .formOptionUsers(request.toEntityList())
                .ticketCount(request.ticketCount())
                .build()
        );

        // when, then
        mvc.perform(post("/api/v1/events/applications")
                        .header("Authorization", "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andDo(
                        document("event/createApplications",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName("Authorization").description("액세스 토큰")
                                ),
                                requestFields(
                                        fieldWithPath("eventId").type(NUMBER).description("행사 id"),
                                        fieldWithPath("ticketCount").type(NUMBER).description("행사 예매 매수"),
                                        fieldWithPath("forms[]").type(ARRAY).description("폼 리스트"),
                                        fieldWithPath("forms[].optionId").type(NUMBER).description("폼 항목 id"),
                                        fieldWithPath("forms[].content").type(STRING).description("폼 항목 답변 내용")
                                )
                        )
                );
    }

}
