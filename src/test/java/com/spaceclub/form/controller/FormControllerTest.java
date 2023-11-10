package com.spaceclub.form.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.form.controller.dto.FormApplicationCreateRequest;
import com.spaceclub.form.controller.dto.FormApplicationGetResponse;
import com.spaceclub.form.controller.dto.FormApplicationGetResponse.FormApplicationItemGetResponse;
import com.spaceclub.form.controller.dto.FormCreateRequest;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOptionType;
import com.spaceclub.form.service.FormService;
import com.spaceclub.form.service.vo.FormCreate;
import com.spaceclub.form.service.vo.FormGet;
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

import static com.spaceclub.form.FormTestFixture.form;
import static com.spaceclub.form.FormTestFixture.formOption1;
import static com.spaceclub.form.FormTestFixture.formOption2;
import static com.spaceclub.form.controller.dto.FormCreateRequest.FormCreateOptionRequest;
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
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
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
        // given
        FormCreateOptionRequest item1 = new FormCreateOptionRequest("이름", FormOptionType.TEXT, true);
        FormCreateOptionRequest item2 = new FormCreateOptionRequest("연락처", FormOptionType.NUMBER, false);
        FormCreateOptionRequest item3 = new FormCreateOptionRequest("인원 수", FormOptionType.TEXT, true);
        List<FormCreateOptionRequest> items = List.of(item1, item2, item3);
        FormCreateRequest formCreateRequest = new FormCreateRequest(1L, "행사에 대한 폼 양식입니다.", true, items);

        Long eventId = 1L;
        Long userId = 1L;
        given(jwtService.verifyUserId(any())).willReturn(userId);
        given(formService.createForm(any(FormCreate.class))).willReturn(eventId);

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
                                        fieldWithPath("managed").type(BOOLEAN).description("관리 모드 여부"),
                                        fieldWithPath("options").type(ARRAY).description("감"),
                                        fieldWithPath("options[].title").type(STRING).description("폼 항목명"),
                                        fieldWithPath("options[].type").type(STRING).description("폼 항목 타입(TEXT, SELECT, RADIO, NUMBER)"),
                                        fieldWithPath("options[].visible").type(BOOLEAN).description("폼 조회 공개 여부")
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
        // given
        Form form = form();
        form.addItems(List.of(formOption1(), formOption2()));
        FormGet formGet = FormGet.builder()
                .title("행사 제목")
                .form(form)
                .build();

        Long userId = 1L;
        given(jwtService.verifyUserId(any())).willReturn(userId);
        given(formService.getForm(any(Long.class), any(Long.class))).willReturn(formGet);

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
                                        fieldWithPath("form").type(OBJECT).description("폼 정보"),
                                        fieldWithPath("form.description").type(STRING).description("폼 설명"),
                                        fieldWithPath("form.options[]").type(ARRAY).description("폼 항목 리스트"),
                                        fieldWithPath("form.options[].id").type(NUMBER).description("폼 항목 id"),
                                        fieldWithPath("form.options[].title").type(STRING).description("폼 항목명"),
                                        fieldWithPath("form.options[].type").type(STRING).description("폼 항목 타입(TEXT, SELECT, RADIO, NUMBER)")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 폼_제출을_통한_행사_신청에_성공한다() throws Exception {
        // given
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
        // given
        FormApplicationGetResponse response = FormApplicationGetResponse.builder()
                .username("박가네")
                .phoneNumber("010-1111-2222")
                .items(List.of(
                        new FormApplicationItemGetResponse("이름", "박가네"),
                        new FormApplicationItemGetResponse("전화번호", "010-1111-2222")
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
