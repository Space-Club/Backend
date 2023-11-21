package com.spaceclub.form.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.form.FormTestFixture;
import com.spaceclub.form.controller.dto.FormSubmitUpdateRequest;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.service.SubmitService;
import com.spaceclub.form.service.vo.FormSubmitGetInfo;
import com.spaceclub.form.service.vo.FormSubmitUpdateInfo;
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
import static com.spaceclub.event.domain.ParticipationStatus.CONFIRMED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SubmitController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        JwtAuthorizationInterceptor.class,
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class SubmitControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private SubmitService submitService;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    void 행사의_신청된_모든_폼_조회에_성공한다() throws Exception {
        // given
        Form form = FormTestFixture.form();
        form.addItems(List.of(FormTestFixture.formOption1(), FormTestFixture.formOption2()));
        Page<EventUser> eventUserPages = new PageImpl<>(List.of(eventUser()));
        FormSubmitGetInfo formSubmitGetInfo = new FormSubmitGetInfo(form, List.of(FormTestFixture.formOptionUser1(), FormTestFixture.formOptionUser2()), eventUserPages);

        given(submitService.getAll(any(), any(Long.class), any(Pageable.class))).willReturn(formSubmitGetInfo);

        // when, then
        mvc.perform(get("/api/v1/events/{eventId}/forms/submit", 1L)
                        .header(AUTHORIZATION, "Access Token")
                        .param("page", "1")
                        .param("size", "3")
                        .param("sort", "id,desc")
                )
                .andExpect(status().isOk())
                .andDo(
                        document("form/getAllSubmit",
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
                                        fieldWithPath("userForms[].participationStatus").type(STRING).description("신청 상태"),
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
        FormSubmitUpdateRequest request = new FormSubmitUpdateRequest(1L, CONFIRMED);
        doNothing().when(submitService).updateStatus(any(FormSubmitUpdateInfo.class));

        // when, then
        mvc.perform(patch("/api/v1/events/{eventId}/forms/submit", 1L)
                        .header(AUTHORIZATION, "Access Token")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andDo(
                        document("form/updateSubmit",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("유저의 액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("eventId").description("행사 id")
                                ),
                                requestFields(
                                        fieldWithPath("formUserId").type(NUMBER).description("행사를 신청한 유저 id"),
                                        fieldWithPath("status").type(STRING).description("행사 신청 상태")
                                )
                        )
                );
    }

}
