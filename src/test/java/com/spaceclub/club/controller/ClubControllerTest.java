package com.spaceclub.club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.club.controller.dto.CreateClubRequest;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubNotice;
import com.spaceclub.club.service.ClubService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClubController.class)
@AutoConfigureRestDocs
class ClubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ClubService clubService;

    @Test
    @DisplayName("클럽 생성에 성공한다")
    @WithMockUser
    void createClubTest() throws Exception {
        // given
        given(clubService.createClub(any(Club.class))).willReturn(
                Club.builder()
                        .name("연사모")
                        .info("연어를 사랑하는 모임")
                        .owner("연어대장")
                        .image("연어.png")
                        .build());
        CreateClubRequest request = new CreateClubRequest("연사모",
                "연어를 사랑하는 모임",
                "연어대장",
                "연어.png");

        // when
        ResultActions result = this.mockMvc.perform(post("/api/v1/clubs")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("club/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(STRING).description("클럽 이름"),
                                fieldWithPath("info").type(STRING).description("클럽 소개"),
                                fieldWithPath("owner").type(STRING).description("클럽 생성자"),
                                fieldWithPath("image").type(STRING).description("클럽 썸네일 이미지")
                        )));
    }

    @Test
    @DisplayName("클럽 조회에 성공한다")
    @WithMockUser
    void getClubTest() throws Exception {
        // given
        given(clubService.getClub(any(Long.class))).willReturn(
                Club.builder()
                        .name("연사모")
                        .info("이곳은 연사모입니다")
                        .image("연어.png")
                        .owner("연어대장")
                        .notices(List.of(new ClubNotice("연사모의 공지사항1")))
                        .build()
        );
        Long clubId = 1L;

        // when
        ResultActions result = this.mockMvc.perform(get("/api/v1/clubs/{clubId}", clubId)
                .with(csrf())
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
    @DisplayName("클럽 삭제에 성공한다")
    @WithMockUser
    void deleteClubTest() throws Exception {
        // given
        Long clubId = 1L;

        // when
        ResultActions result = this.mockMvc.perform(delete("/api/v1/clubs/{clubId}", clubId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("club/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

}
