package com.spaceclub.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.board.controller.domain.Post;
import com.spaceclub.board.controller.dto.PostRequest;
import com.spaceclub.board.controller.dto.PostUpdateRequest;
import com.spaceclub.board.service.PostService;
import com.spaceclub.board.service.vo.PostInfo;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.PUT;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = PostController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        WebConfig.class,
                        AuthorizationInterceptor.class,
                        AuthenticationInterceptor.class
                })
        })
@AutoConfigureRestDocs
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private PostService postService;

    @MockBean
    private S3ImageUploader imageUploader;

    @MockBean
    private UserArgumentResolver userArgumentResolver;

    @Test
    @WithMockUser
    void 게시글_전체_조회에_성공한다() throws Exception {
        List<Post> posts = List.of(
                Post.builder()
                        .id(1L)
                        .title("title1")
                        .content("content1")
                        .postImageUrl("postImageUrl1")
                        .authorId(1L)
                        .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .lastModifiedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .build(),
                Post.builder()
                        .id(2L)
                        .title("title2")
                        .content("content2")
                        .postImageUrl(null)
                        .authorId(1L)
                        .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .lastModifiedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .build(),
                Post.builder()
                        .id(3L)
                        .title("title3")
                        .content("content3")
                        .postImageUrl("postImageUrl3")
                        .authorId(2L)
                        .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .lastModifiedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .build()
        );

        UserProfile userProfile = new UserProfile("authorName", "authorPhoneNumber", "authorEmail", "authorImageUrl");
        List<PostInfo> postInfos = posts.stream().map(post -> PostInfo.of(post, userProfile)).toList();
        Page<PostInfo> postPages = new PageImpl<>(postInfos);

        final String bucketUrl = "spaceclub.site/";
        given(imageUploader.getBucketUrl()).willReturn(bucketUrl);
        given(postService.getClubBoardPostsByPaging(any(), any())).willReturn(postPages);
        Long clubId = 1L;

        mockMvc.perform(get("/api/v1/boards/posts/{clubId}", clubId)
                        .header(AUTHORIZATION, "access token")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(posts.size()))
                .andExpect(jsonPath("$.data[0].createdDate").value("2024-01-01T00:00:00"))
                .andExpect(jsonPath("$.data[0].lastModifiedDate").value("2024-01-01T00:00:00"))
                .andExpect(jsonPath("$.data[0].postImageUrl").value(bucketUrl + posts.get(0).getPostImageUrl()))
                .andExpect(jsonPath("$.data[0].authorImageUrl").value(userProfile.profileImageUrl()))
                .andExpect(jsonPath("$.data[0].author").value(userProfile.username()))
                .andExpect(jsonPath("$.data[1].postImageUrl").doesNotExist())
                .andExpect(jsonPath("$.data[2].postImageUrl").value(bucketUrl + posts.get(2).getPostImageUrl()))
                .andExpect(jsonPath("$.pageData.first").value(true))
                .andExpect(jsonPath("$.pageData.last").value(true))
                .andExpect(jsonPath("$.pageData.pageNumber").value(0))
                .andExpect(jsonPath("$.pageData.size").value(posts.size()))
                .andExpect(jsonPath("$.pageData.totalPages").value(1))
                .andExpect(jsonPath("$.pageData.totalElements").value(posts.size()))
                .andDo(
                        document("post/getPostsByPaging",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("clubId").description("클럽 아이디")
                                ),
                                queryParameters(
                                        parameterWithName("page").optional().description("페이지"),
                                        parameterWithName("size").optional().description("페이지 내 개수, default 10"),
                                        parameterWithName("sort").optional().description("정렬 방법(ex. id,desc), default id,desc")
                                ),
                                responseFields(
                                        fieldWithPath("data").type(ARRAY).description("페이지 내 게시글 정보"),
                                        fieldWithPath("data[].postId").type(NUMBER).description("게시글 아이디"),
                                        fieldWithPath("data[].title").type(STRING).description("게시글 제목"),
                                        fieldWithPath("data[].content").type(STRING).description("게시글 내용"),
                                        fieldWithPath("data[].authorId").type(NUMBER).description("게시글 작성자 아이디"),
                                        fieldWithPath("data[].author").type(STRING).description("게시글 작성자 이름"),
                                        fieldWithPath("data[].authorImageUrl").type(STRING).description("게시글 작성자 프로필 이미지 URL"),
                                        fieldWithPath("data[].postImageUrl").type(STRING).optional().description("게시글 이미지 URL"),
                                        fieldWithPath("data[].createdDate").type(STRING).description("게시글 작성일"),
                                        fieldWithPath("data[].lastModifiedDate").type(STRING).description("게시글 마지막 수정일"),
                                        fieldWithPath("pageData").type(OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageData.first").type(BOOLEAN).description("첫 페이지 여부"),
                                        fieldWithPath("pageData.last").type(BOOLEAN).description("마지막 페이지 여부"),
                                        fieldWithPath("pageData.pageNumber").type(NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("pageData.size").type(NUMBER).description("페이지 내 개수"),
                                        fieldWithPath("pageData.totalPages").type(NUMBER).description("총 페이지 개수"),
                                        fieldWithPath("pageData.totalElements").type(NUMBER).description("총 이벤트 개수")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 게시글_단건_조회에_성공한다() throws Exception {
        Post post = Post.builder()
                .id(1L)
                .title("title1")
                .content("content1")
                .postImageUrl("postImageUrl1")
                .authorId(1L)
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .lastModifiedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
        UserProfile userProfile = new UserProfile("authorName", "authorPhoneNumber", "authorEmail", "authorImageUrl");
        given(postService.getClubBoardPost(any(), any())).willReturn(PostInfo.of(post, userProfile));
        String bucketUrl = "spaceclub.site/";
        given(imageUploader.getBucketUrl()).willReturn(bucketUrl);
        Long clubId = 1L;
        Long postId = 1L;

        mockMvc.perform(get("/api/v1/boards/posts/{clubId}/{postId}", clubId, postId)
                        .header(AUTHORIZATION, "access token")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.authorId").value(post.getAuthorId()))
                .andExpect(jsonPath("$.author").value(userProfile.username()))
                .andExpect(jsonPath("$.authorImageUrl").value(userProfile.profileImageUrl()))
                .andExpect(jsonPath("$.postImageUrl").value(bucketUrl + post.getPostImageUrl()))
                .andExpect(jsonPath("$.createdDate").value("2024-01-01T00:00:00"))
                .andExpect(jsonPath("$.lastModifiedDate").value("2024-01-01T00:00:00"))
                .andDo(
                        document("post/getSinglePost",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("clubId").description("클럽 아이디"),
                                        parameterWithName("postId").description("게시글 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("postId").type(NUMBER).description("게시글 아이디"),
                                        fieldWithPath("title").type(STRING).description("게시글 제목"),
                                        fieldWithPath("content").type(STRING).description("게시글 내용"),
                                        fieldWithPath("authorId").type(NUMBER).description("게시글 작성자 아이디"),
                                        fieldWithPath("author").type(STRING).description("게시글 작성자 이름"),
                                        fieldWithPath("authorImageUrl").type(STRING).description("게시글 작성자 프로필 이미지 URL"),
                                        fieldWithPath("postImageUrl").type(STRING).optional().description("게시글 이미지 URL"),
                                        fieldWithPath("createdDate").type(STRING).description("게시글 작성일"),
                                        fieldWithPath("lastModifiedDate").type(STRING).description("게시글 마지막 수정일")
                                )
                        )
                );

    }

    @Test
    @WithMockUser
    void 파일을_첨부한_게시글_생성에_성공한다() throws Exception {
        Long clubId = 1L;
        Long postId = 1L;
        given(postService.createClubBoardPost(any(), any(), any(), any())).willReturn(1L);
        PostRequest postRequest = new PostRequest("title1", "content1");
        MockMultipartFile multipartFile =
                new MockMultipartFile("image", "image.png", MediaType.IMAGE_PNG_VALUE, "content".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile postRequestFile =
                new MockMultipartFile("postRequest", "", MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(postRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/v1/boards/posts/{clubId}", clubId)
                        .file(multipartFile)
                        .file(postRequestFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(AUTHORIZATION, "access token")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/v1/boards/posts/%d/%d".formatted(clubId, postId)))
                .andDo(
                        document("post/createWithImage",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("clubId").description("클럽 아이디")
                                ),
                                requestParts(
                                        partWithName("postRequest").description("게시글 제목 및 내용"),
                                        partWithName("image").description("첨부 이미지").optional()
                                ),
                                requestPartFields("postRequest",
                                        fieldWithPath("title").type(STRING).description("게시글 제목"),
                                        fieldWithPath("content").type(STRING).description("게시글 내용")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 파일을_첨부하지_않은_게시글_생성에_성공한다() throws Exception {
        Long clubId = 1L;
        Long postId = 1L;
        given(postService.createClubBoardPost(any(), any(), any(), any())).willReturn(1L);
        PostRequest postRequest = new PostRequest("title1", "content1");
        MockMultipartFile postRequestFile =
                new MockMultipartFile("postRequest", "", MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(postRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/v1/boards/posts/{clubId}", clubId)
                        .file(postRequestFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(AUTHORIZATION, "access token")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/v1/boards/posts/%d/%d".formatted(clubId, postId)))

                .andDo(
                        document("post/createWithoutImage",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("clubId").description("클럽 아이디")
                                ),
                                requestParts(
                                        partWithName("postRequest").description("게시글 제목 및 내용")
                                ),
                                requestPartFields("postRequest",
                                        fieldWithPath("title").type(STRING).description("게시글 제목"),
                                        fieldWithPath("content").type(STRING).description("게시글 내용")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 게시글_수정에_성공한다() throws Exception {
        Long postId = 1L;
        PostUpdateRequest postRequest = new PostUpdateRequest("title1", "content1", true);
        doNothing().when(postService).updateClubBoardPost(any(Long.class), any(PostUpdateRequest.class), any(String.class));

        MockMultipartFile multipartFile =
                new MockMultipartFile("image", "image.png", MediaType.IMAGE_PNG_VALUE, "content".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile postRequestFile =
                new MockMultipartFile("postRequest", "", MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(postRequest).getBytes(StandardCharsets.UTF_8));

        var builder = (MockMultipartHttpServletRequestBuilder) multipart("/api/v1/boards/posts/{postId}", postId)
                .with(request -> {
                    request.setMethod(PUT.name());
                    return request;
                });

        mockMvc.perform((builder)
                        .file(multipartFile)
                        .file(postRequestFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(AUTHORIZATION, "access token")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("post/update",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("postId").description("게시글 아이디")
                                ),
                                requestParts(
                                        partWithName("postRequest").description("게시글 제목 및 내용"),
                                        partWithName("image").description("첨부 이미지").optional()
                                ),
                                requestPartFields("postRequest",
                                        fieldWithPath("title").type(STRING).description("게시글 제목"),
                                        fieldWithPath("content").type(STRING).description("게시글 내용"),
                                        fieldWithPath("doesPostImageExist").type(BOOLEAN).description("게시글 이미지 존재 여부")
                                )
                        )
                );
    }

    @Test
    @WithMockUser
    void 게시글_삭제에_성공한다() throws Exception {
        Long postId = 1L;
        doNothing().when(postService).deleteClubBoardPost(any(Long.class));

        mockMvc.perform(delete("/api/v1/boards/posts/{postId}", postId)
                        .header(AUTHORIZATION, "access token")
                        .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andDo(
                        document("post/delete",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("postId").description("게시글 아이디")
                                ),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                )
                        )
                );
    }

}
