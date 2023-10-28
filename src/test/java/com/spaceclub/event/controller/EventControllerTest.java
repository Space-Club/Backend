package com.spaceclub.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.event.controller.dto.EventCreateRequest;
import com.spaceclub.event.domain.Category;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.service.EventService;
import com.spaceclub.global.S3ImageUploader;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
        MockMultipartFile poster = new MockMultipartFile(
                "poster",
                "image.png",
                MediaType.IMAGE_JPEG_VALUE,
                "<<jpeg data>>".getBytes());

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(eventCreateRequest)
        );

        final String posterUrl = "image.jpeg";
        given(uploader.uploadImage(any(MultipartFile.class))).willReturn(posterUrl);
        given(eventService.create(any(Event.class), any(Long.class))).willReturn(1L);

        // when
        ResultActions actions = mvc.perform(multipart("/api/v1/events")
                .file(poster)
                .file(request)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        );

        // then
        actions
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/v1/events/1"))
                .andDo(document("events/create",
                        requestParts(
                                partWithName("request").description("행사 생성 관련 정보"),
                                partWithName("poster").description("포스터 사진")
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

}
