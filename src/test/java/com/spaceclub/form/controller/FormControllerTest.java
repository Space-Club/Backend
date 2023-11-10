package com.spaceclub.form.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.form.controller.FormApplicationGetResponse.FormApplicationItemGetResponse;
import com.spaceclub.form.controller.dto.FormApplicationCreateRequest;
import com.spaceclub.form.controller.dto.FormCreateRequest;
import com.spaceclub.form.controller.dto.FormGetResponse;
import com.spaceclub.form.controller.dto.FormGetResponse.EventResponse;
import com.spaceclub.form.controller.dto.FormGetResponse.FormItemResponse;
import com.spaceclub.form.controller.dto.FormGetResponse.FormResponse;
import com.spaceclub.form.service.FormService;
import com.spaceclub.global.jwt.service.JwtService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.spaceclub.form.controller.dto.FormCreateRequest.FormItemRequest;
import static com.spaceclub.form.controller.dto.FormCreateRequest.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FormController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class FormControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private FormService formService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void 관리자가_폼_양식_생성에_성공한다() throws Exception {
        FormCreateRequest formCreateRequest = builder()
                .eventId(1L)
                .description("행사에 대한 폼 양식입니다.")
                .items(List.of(
                        FormItemRequest.builder().name("이름").build(),
                        FormItemRequest.builder().name("연락처").build(),
                        FormItemRequest.builder().name("인원 수").build()
                ))
                .build();

        Long eventId = 1L;
        Long userId = 1L;
        given(jwtService.verifyUserId(any())).willReturn(userId);
        given(formService.createForm()).willReturn(eventId);

        // when, then
        mvc.perform(post("/api/v1/events/forms")
                        .header("Authorization", "Access Token")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(formCreateRequest))
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andDo(
                        document("form/create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName("Authorization").description("액세스 토큰")
                                ),
                                requestFields(
                                        fieldWithPath("eventId").type(NUMBER).description("행사 id"),
                                        fieldWithPath("description").type(STRING).description("폼 설명"),
                                        fieldWithPath("items").type(ARRAY).description("폼 항목 리스트"),
                                        fieldWithPath("items[].name").type(STRING).description("폼 항목명")
                                ),
                                responseHeaders(
                                        headerWithName("Location").description("생성된 폼의 행사 조회 URI")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 폼_양식_조회에_성공한다() throws Exception {
        FormGetResponse formGetResponse = FormGetResponse.builder()
                .event(EventResponse.builder().title("행사 제목").posterImageUrl("행사 포스터 이미지 URL").build())
                .form(FormResponse.builder()
                        .description("폼에 대한 설명")
                        .items(List.of(
                                FormItemResponse.builder().id(1L).name("이름").build(),
                                FormItemResponse.builder().id(2L).name("연락처").build()
                        )).build())
                .build();

        Long userId = 1L;
        given(jwtService.verifyUserId(any())).willReturn(userId);
        given(formService.getForm()).willReturn(formGetResponse);

        // when, then
        mvc.perform(get("/api/v1/events/{eventId}/forms", 1L)
                        .header("Authorization", "Access Token")
                )
                .andExpect(status().isOk())
                .andDo(
                        document("form/get",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName("Authorization").description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("eventId").description("행사 id")
                                ),
                                responseFields(
                                        fieldWithPath("event").type(OBJECT).description("행사 정보"),
                                        fieldWithPath("event.title").type(STRING).description("행사 제목"),
                                        fieldWithPath("event.posterImageUrl").type(STRING).description("행사 포스터 URL"),
                                        fieldWithPath("form").type(OBJECT).description("폼 정보"),
                                        fieldWithPath("form.description").type(STRING).description("폼 설명"),
                                        fieldWithPath("form.items[]").type(ARRAY).description("폼 항목 리스트"),
                                        fieldWithPath("form.items[].id").type(NUMBER).description("폼 항목 id"),
                                        fieldWithPath("form.items[].name").type(STRING).description("폼 항목명")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 폼_제출을_통한_행사_신청에_성공한다() throws Exception {
        FormApplicationCreateRequest request = FormApplicationCreateRequest.builder()
                .id(1L)
                .name("이름")
                .build();

        Long userId = 1L;
        given(jwtService.verifyUserId(any())).willReturn(userId);
        doNothing().when(formService).createApplicationForm();

        // when, then
        mvc.perform(post("/api/v1/events/forms/applications")
                        .header("Authorization", "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(List.of(request)))
                        .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andDo(
                        document("form/createApplications",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName("Authorization").description("액세스 토큰")
                                ),
                                requestFields(
                                        fieldWithPath("[]").type(ARRAY).description("폼 항목 리스트"),
                                        fieldWithPath("[].id").type(NUMBER).description("폼 항목 id"),
                                        fieldWithPath("[].name").type(STRING).description("폼 항목명")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 행사의_신청된_모든_폼_조회에_성공한다() throws Exception {
        FormApplicationGetResponse response = FormApplicationGetResponse.builder()
                .username("박가네")
                .phoneNumber("010-1111-2222")
                .items(List.of(
                        FormApplicationItemGetResponse.builder().name("이름").content("박가네").build(),
                        FormApplicationItemGetResponse.builder().name("전화번호").content("010-1111-2222").build()
                ))
                .build();


        Long userId = 1L;
        given(jwtService.verifyUserId(any())).willReturn(userId);
        given(formService.getAllForms()).willReturn(List.of(response));

        // when, then
        mvc.perform(get("/api/v1/events/{eventId}/forms/applications", 1L)
                        .header("Authorization", "Access Token")
                )
                .andExpect(status().isOk())
                .andDo(
                        document("form/getAll",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName("Authorization").description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("eventId").description("행사 id")
                                ),
                                responseFields(
                                        fieldWithPath("[]").type(ARRAY).description("신청된 폼 리스트"),
                                        fieldWithPath("[].username").type(STRING).description("유저 이름"),
                                        fieldWithPath("[].phoneNumber").type(STRING).description("유저 핸드폰 번호"),
                                        fieldWithPath("[].items[]").type(ARRAY).description("폼 항목 리스트"),
                                        fieldWithPath("[].items[].name").type(STRING).description("폼 항목명"),
                                        fieldWithPath("[].items[].content").type(STRING).description("폼 항목 내용")
                                )
                        )
                );
    }

}
