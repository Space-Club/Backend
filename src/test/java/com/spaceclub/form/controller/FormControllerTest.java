package com.spaceclub.form.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.event.domain.ApplicationStatus;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.form.FormTestFixture;
import com.spaceclub.form.controller.dto.FormApplicationStatusUpdateRequest;
import com.spaceclub.form.controller.dto.FormCreateRequest;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOptionType;
import com.spaceclub.form.service.FormService;
import com.spaceclub.form.service.vo.FormApplicationGetInfo;
import com.spaceclub.form.service.vo.FormCreate;
import com.spaceclub.form.service.vo.FormGet;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.spaceclub.event.EventTestFixture.eventUser;
import static com.spaceclub.event.domain.ApplicationStatus.CONFIRMED;
import static com.spaceclub.form.controller.dto.FormCreateRequest.FormCreateOptionRequest;
import static com.spaceclub.user.UserTestFixture.user1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = FormController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        JwtAuthorizationInterceptor.class,
                })
        })
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
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    void 관리자가_폼_양식_생성에_성공한다() throws Exception {
        // given
        FormCreateOptionRequest item1 = new FormCreateOptionRequest("이름", FormOptionType.TEXT);
        FormCreateOptionRequest item2 = new FormCreateOptionRequest("연락처", FormOptionType.NUMBER);
        FormCreateOptionRequest item3 = new FormCreateOptionRequest("인원 수", FormOptionType.TEXT);
        List<FormCreateOptionRequest> items = List.of(item1, item2, item3);
        FormCreateRequest formCreateRequest = new FormCreateRequest(1L, "행사에 대한 폼 양식입니다.", true, items);

        Long eventId = 1L;
        given(formService.createForm(any(FormCreate.class))).willReturn(eventId);

        // when, then
        mvc.perform(post("/api/v1/events/forms")
                        .header(AUTHORIZATION, "Access Token")
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
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                requestFields(
                                        fieldWithPath("eventId").type(NUMBER).description("행사 id"),
                                        fieldWithPath("description").type(STRING).description("폼 설명"),
                                        fieldWithPath("managed").type(BOOLEAN).description("관리 모드 여부"),
                                        fieldWithPath("options[]").type(ARRAY).description("감"),
                                        fieldWithPath("options[].title").type(STRING).description("폼 항목명"),
                                        fieldWithPath("options[].type").type(STRING).description("폼 항목 타입(TEXT, SELECT, RADIO, NUMBER)")
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
        Form form = FormTestFixture.form();
        form.addItems(List.of(FormTestFixture.formOption1(), FormTestFixture.formOption2()));
        FormGet formGet = FormGet.builder()
                .title("행사 제목")
                .form(form)
                .user(user1())
                .build();
        given(formService.getForm(any(), any(Long.class))).willReturn(formGet);

        // when, then
        mvc.perform(get("/api/v1/events/{eventId}/forms", 1L)
                        .header(AUTHORIZATION, "Access Token")
                )
                .andExpect(status().isOk())
                .andDo(
                        document("form/get",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("eventId").description("행사 id")
                                ),
                                responseFields(
                                        fieldWithPath("event").type(OBJECT).description("행사 정보"),
                                        fieldWithPath("event.title").type(STRING).description("행사 제목"),
                                        fieldWithPath("user").type(OBJECT).description("유저 정보"),
                                        fieldWithPath("user.name").type(STRING).description("유저 이름"),
                                        fieldWithPath("user.phoneNumber").type(STRING).description("유저 전화번호"),
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
    void 행사의_신청된_모든_폼_조회에_성공한다() throws Exception {
        // given
        Form form = FormTestFixture.form();
        form.addItems(List.of(FormTestFixture.formOption1(), FormTestFixture.formOption2()));
        Page<EventUser> eventUserPages = new PageImpl<>(List.of(eventUser()));
        FormApplicationGetInfo formApplicationGetInfo = new FormApplicationGetInfo(form, List.of(FormTestFixture.formOptionUser1(), FormTestFixture.formOptionUser2()), eventUserPages);

        Long userId = 1L;
        given(formService.getApplicationForms(any(), any(Long.class), any(Pageable.class))).willReturn(formApplicationGetInfo);

        // when, then
        mvc.perform(get("/api/v1/events/{eventId}/forms/applications", 1L)
                        .header(AUTHORIZATION, "Access Token")
                        .param("page", "1")
                        .param("size", "3")
                        .param("sort", "id,desc")
                )
                .andExpect(status().isOk())
                .andDo(
                        document("form/getAllApplications",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("eventId").description("행사 id")
                                ),
                                queryParameters(
                                        parameterWithName("page").description("페이지"),
                                        parameterWithName("size").description("페이지 내 개수"),
                                        parameterWithName("sort").description("정렬 방법(ex. id,desc)")
                                ),
                                responseFields(
                                        fieldWithPath("formInfo").type(OBJECT).description("폼 정보"),
                                        fieldWithPath("formInfo.count").type(NUMBER).description("폼 개수"),
                                        fieldWithPath("formInfo.optionTitles[]").type(ARRAY).description("폼 옵션명 리스트"),
                                        fieldWithPath("formInfo.managed").type(BOOLEAN).description("관리 모드 여부"),
                                        fieldWithPath("userForms[]").type(ARRAY).description("유저 폼 리스트"),
                                        fieldWithPath("userForms[].userId").type(NUMBER).description("폼의 유저 id"),
                                        fieldWithPath("userForms[].options[]").type(ARRAY).description("폼 옵션 리스트"),
                                        fieldWithPath("userForms[].options[].title").type(STRING).description("폼 옵션명"),
                                        fieldWithPath("userForms[].options[].content").type(STRING).description("폼 옵션 내용"),
                                        fieldWithPath("userForms[].applicationStatus").type(STRING).description("신청 상태"),
                                        fieldWithPath("pageData").type(OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageData.first").type(BOOLEAN).description("첫 페이지 여부"),
                                        fieldWithPath("pageData.last").type(BOOLEAN).description("마지막 페이지 여부"),
                                        fieldWithPath("pageData.pageNumber").type(NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("pageData.size").type(NUMBER).description("페이지 내 개수"),
                                        fieldWithPath("pageData.totalPages").type(NUMBER).description("총 페이지 개수"),
                                        fieldWithPath("pageData.totalElements").type(NUMBER).description("총 행사 개수")
                                )

                        )
                );
    }

    @Test
    @WithMockUser
    void 행사의_신청_상태_변경에_성공한다() throws Exception {
        // given
        FormApplicationStatusUpdateRequest request = new FormApplicationStatusUpdateRequest(CONFIRMED);
        doNothing().when(formService).updateApplicationStatus(any(Long.class), any(Long.class), any(ApplicationStatus.class));

        // when, then
        mvc.perform(patch("/api/v1/events/{eventId}/forms/applications-status", 1L)
                        .header(AUTHORIZATION, "Access Token")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andDo(
                        document("form/updateApplicationStatus",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("유저의 액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("eventId").description("행사 id")
                                ),
                                requestFields(
                                        fieldWithPath("status").type(STRING).description("행사 신청 상태")
                                )
                        )
                );
    }

}
