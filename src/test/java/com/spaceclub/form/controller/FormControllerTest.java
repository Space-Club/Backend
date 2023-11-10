package com.spaceclub.form.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.form.controller.dto.FormCreateRequest;
import com.spaceclub.form.service.FormService;
import com.spaceclub.global.jwt.service.JwtService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.spaceclub.form.controller.dto.FormCreateRequest.FormItemRequest;
import static com.spaceclub.form.controller.dto.FormCreateRequest.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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

}
