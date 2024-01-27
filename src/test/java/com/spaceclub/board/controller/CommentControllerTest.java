package com.spaceclub.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.board.controller.domain.Comment;
import com.spaceclub.board.controller.dto.CommentRequest;
import com.spaceclub.board.service.CommentService;
import com.spaceclub.board.service.vo.CommentInfo;
import com.spaceclub.global.UserArgumentResolver;
import com.spaceclub.global.config.WebConfig;
import com.spaceclub.global.interceptor.AuthenticationInterceptor;
import com.spaceclub.global.interceptor.AuthorizationInterceptor;
import com.spaceclub.global.s3.S3ImageUploader;
import com.spaceclub.user.service.vo.UserProfile;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = CommentController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        WebConfig.class,
                        AuthorizationInterceptor.class,
                        AuthenticationInterceptor.class
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private S3ImageUploader s3ImageUploader;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    void 댓글_전체_조회에_성공한다() throws Exception {
        Long postId = 1L;
        List<Comment> comments = List.of(
                Comment.builder()
                        .id(1L)
                        .content("content1")
                        .authorId(1L)
                        .isPrivate(false)
                        .postId(postId)
                        .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .lastModifiedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .build(),
                Comment.builder()
                        .id(2L)
                        .content("content2")
                        .authorId(1L)
                        .isPrivate(false)
                        .postId(postId)
                        .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .lastModifiedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .build(),
                Comment.builder()
                        .id(3L)
                        .content("content3")
                        .authorId(2L)
                        .isPrivate(true)
                        .postId(postId)
                        .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .lastModifiedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .build()
        );
        UserProfile userProfile = new UserProfile("authorName", "authorPhoneNumber", "authorEmail", "authorImageUrl");
        List<CommentInfo> commentInfos = comments.stream().map(comment -> CommentInfo.of(comment, userProfile)).toList();
        Page<CommentInfo> commentPages = new PageImpl<>(commentInfos);
        given(commentService.getComments(any(Long.class), any(Pageable.class))).willReturn(commentPages);

        mockMvc.perform(get("/api/v1/boards/posts/{postId}/comments", postId)
                        .header(AUTHORIZATION, "access token")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(comments.size()))
                .andExpect(jsonPath("$.pageData.first").value(true))
                .andExpect(jsonPath("$.pageData.last").value(true))
                .andExpect(jsonPath("$.pageData.pageNumber").value(0))
                .andExpect(jsonPath("$.pageData.size").value(comments.size()))
                .andExpect(jsonPath("$.pageData.totalElements").value(comments.size()))
                .andDo(
                        document("comment/getCommentsByPaging",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("postId").description("게시글 아이디")
                                ),
                                queryParameters(
                                        parameterWithName("page").optional().description("페이지"),
                                        parameterWithName("size").optional().description("페이지 내 개수, default 10"),
                                        parameterWithName("sort").optional().description("정렬 방법(ex. id,desc), default createdAt,desc")
                                ),
                                responseFields(
                                        fieldWithPath("data").type(ARRAY).description("페이지 내 댓글 정보"),
                                        fieldWithPath("data[].commentId").type(NUMBER).description("댓글 아이디"),
                                        fieldWithPath("data[].content").type(STRING).description("댓글 내용"),
                                        fieldWithPath("data[].authorId").type(NUMBER).description("댓글 작성자 아이디"),
                                        fieldWithPath("data[].author").type(STRING).description("댓글 작성자 이름"),
                                        fieldWithPath("data[].authorImageUrl").type(STRING).description("댓글 작성자 프로필 이미지 URL"),
                                        fieldWithPath("data[].createdDate").type(STRING).description("댓글 작성일"),
                                        fieldWithPath("data[].lastModifiedDate").type(STRING).description("댓글 마지막 수정일"),
                                        fieldWithPath("data[].isPrivate").type(BOOLEAN).description("비밀 댓글 여부"),
                                        fieldWithPath("pageData").type(OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageData.totalPages").type(NUMBER).description("총 페이지"),
                                        fieldWithPath("pageData.first").type(BOOLEAN).description("첫 페이지 여부"),
                                        fieldWithPath("pageData.last").type(BOOLEAN).description("마지막 페이지 여부"),
                                        fieldWithPath("pageData.pageNumber").type(NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("pageData.size").type(NUMBER).description("페이지 size (default 10)"),
                                        fieldWithPath("pageData.totalElements").type(NUMBER).description("페이지 내 댓글 개수")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 댓글_단건_조회에_성공한다() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;
        Comment comment = Comment.builder()
                .id(commentId)
                .content("content1")
                .authorId(1L)
                .isPrivate(false)
                .postId(postId)
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .lastModifiedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
        UserProfile userProfile = new UserProfile("authorName", "authorPhoneNumber", "authorEmail", "authorImageUrl");
        given(commentService.getComment(any(Long.class))).willReturn(CommentInfo.of(comment, userProfile));

        String bucketUrl = "spaceclub.site/";
        given(s3ImageUploader.getBucketUrl()).willReturn(bucketUrl);

        mockMvc.perform(get("/api/v1/boards/posts/comments/{commentId}", commentId)
                        .header(AUTHORIZATION, "access token")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(commentId))
                .andExpect(jsonPath("$.content").value(comment.getContent()))
                .andExpect(jsonPath("$.authorId").value(comment.getAuthorId()))
                .andExpect(jsonPath("$.author").value(userProfile.username()))
                .andExpect(jsonPath("$.authorImageUrl").value(userProfile.profileImageUrl()))
                .andExpect(jsonPath("$.createdDate").value("2024-01-01T00:00:00"))
                .andExpect(jsonPath("$.lastModifiedDate").value("2024-01-01T00:00:00"))
                .andExpect(jsonPath("$.isPrivate").value(comment.isPrivate()))
                .andDo(
                        document("comment/getSingleComment",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("commentId").description("댓글 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("commentId").type(NUMBER).description("댓글 아이디"),
                                        fieldWithPath("content").type(STRING).description("댓글 내용"),
                                        fieldWithPath("authorId").type(NUMBER).description("댓글 작성자 아이디"),
                                        fieldWithPath("author").type(STRING).description("댓글 작성자 이름"),
                                        fieldWithPath("authorImageUrl").type(STRING).description("댓글 작성자 프로필 이미지 URL"),
                                        fieldWithPath("createdDate").type(STRING).description("댓글 작성일"),
                                        fieldWithPath("lastModifiedDate").type(STRING).description("댓글 마지막 수정일"),
                                        fieldWithPath("isPrivate").type(BOOLEAN).description("비밀 댓글 여부")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 댓글_생성에_성공한다() throws Exception {
        Long commentId = 1L;
        Long postId = 1L;
        CommentRequest commentRequest = new CommentRequest("content1", false);

        given(commentService.createComment(any(), any(), any())).willReturn(commentId);

        mockMvc.perform(post("/api/v1/boards/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest))
                        .header(AUTHORIZATION, "access token")
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/v1/boards/posts/comments/%d".formatted(commentId)))
                .andDo(
                        document("comment/create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("postId").description("게시글 아이디")
                                ),
                                requestFields(
                                        fieldWithPath("content").type(STRING).description("댓글 내용"),
                                        fieldWithPath("isPrivate").type(BOOLEAN).description("비밀 댓글 여부")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 댓글_수정에_성공한다() throws Exception {
        Long commentId = 1L;
        CommentRequest commentRequest = new CommentRequest("content1", false);

        doNothing().when(commentService).updateComment(any(Long.class), any(CommentRequest.class));

        mockMvc.perform(put("/api/v1/boards/posts/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest))
                        .header(AUTHORIZATION, "access token")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(
                        document("comment/update",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("commentId").description("댓글 아이디")
                                ),
                                requestFields(
                                        fieldWithPath("content").type(STRING).description("댓글 내용"),
                                        fieldWithPath("isPrivate").type(BOOLEAN).description("비밀 댓글 여부")
                                )
                        )
                );

    }

    @Test
    @WithMockUser
    void 댓글_삭제에_성공한다() throws Exception {
        Long commentId = 1L;

        doNothing().when(commentService).deleteComment(any(Long.class));

        mockMvc.perform(delete("/api/v1/boards/posts/comments/{commentId}", commentId)
                        .header(AUTHORIZATION, "access token")
                        .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andDo(
                        document("comment/delete",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("commentId").description("댓글 아이디")
                                )
                        )
                );
    }

}
