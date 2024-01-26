package com.spaceclub.board.service;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import com.spaceclub.board.controller.domain.Post;
import com.spaceclub.board.controller.dto.PostRequest;
import com.spaceclub.board.controller.dto.PostUpdateRequest;
import com.spaceclub.board.repository.PostRepository;
import com.spaceclub.board.service.vo.PostInfo;
import com.spaceclub.user.service.UserService;
import com.spaceclub.user.service.vo.UserProfile;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private EntityManager entityManager;

    @MockBean
    private UserService userService;

    @AfterEach
    void resetAutoIncrementId() {
        entityManager.createNativeQuery("ALTER TABLE POST ALTER COLUMN POST_ID RESTART WITH 1")
                .executeUpdate();
    }

    @BeforeEach
    void setUp() {
        Post post1 = getPost("1", 1L);
        Post post2 = getPost("2", 1L);
        Post post3 = getPost("3", 2L);

        postRepository.saveAll(List.of(post1, post2, post3));
    }

    private Post getPost(String suffix, Long clubId) {
        return Post.builder()
                .title("title" + suffix)
                .content("content" + suffix)
                .postImageUrl("imageUrl" + suffix)
                .authorId(1L)
                .clubId(clubId)
                .build();
    }

    @Test
    void 게시글_페이징_조회에_성공한다() {
        // given
        UserProfile userProfile = new UserProfile("username", "phoneNumber", "email", "profileImageUrl");
        given(userService.getProfile(1L)).willReturn(userProfile);

        PageRequest pageRequest = PageRequest.of(0, 10);
        Long clubId = 1L;

        // when
        Page<PostInfo> posts = postService.getClubBoardPostsByPaging(pageRequest, clubId);

        // then
        assertAll(
                () -> assertThat(posts).hasSize(2),
                () -> assertThat(posts.getTotalElements()).isEqualTo(2),
                () -> assertThat(posts.isFirst()).isTrue(),
                () -> assertThat(posts.isLast()).isTrue()
        );
    }

    @Test
    void 게시글_단건_조회에_성공한다() {
        // given
        UserProfile userProfile = new UserProfile("username", "phoneNumber", "email", "profileImageUrl");
        given(userService.getProfile(1L)).willReturn(userProfile);

        Long clubId = 1L;
        Long postId = 2L;

        // when
        PostInfo clubBoardPost = postService.getClubBoardPost(clubId, postId);

        // then
        assertAll(
                () -> assertThat(clubBoardPost).extracting("content").isEqualTo("content2"),
                () -> assertThat(clubBoardPost).extracting("postImageUrl").isEqualTo("imageUrl2"),
                () -> assertThat(clubBoardPost).extracting("author").isEqualTo("username"),
                () -> assertThat(clubBoardPost).extracting("authorImageUrl").isEqualTo("profileImageUrl")
        );
    }

    @Test
    void 게시글_생성에_성공한다() {
        // given
        Long clubId = 2L;
        String title = "title test";
        String content = "content test";
        PostRequest postRequest = new PostRequest(title, content);
        Long userId = 1L;
        String postImageUrl = "test.com";

        // when
        Long createdPostId = postService.createClubBoardPost(clubId, postRequest, userId, postImageUrl);
        Post post = postRepository.findById(createdPostId).orElseThrow();

        // then
        assertAll(
                () -> assertThat(post.getClubId()).isEqualTo(clubId),
                () -> assertThat(post.getTitle()).isEqualTo(title),
                () -> assertThat(post.getContent()).isEqualTo(content),
                () -> assertThat(post.getPostImageUrl()).isEqualTo(postImageUrl)
        );
    }

    @Test
    void 게시글에_사진과_내용_업데이트에_성공한다() {
        // given
        Long postId = 1L;
        String title = "title test";
        String content = "content test";
        boolean imageExist = true;
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(title, content, imageExist);
        String postImageUrl = "test.com";

        // when
        postService.updateClubBoardPost(postId, postUpdateRequest, postImageUrl);
        Post post = postRepository.findById(postId).orElseThrow();

        // then
        assertAll(
                () -> assertThat(post.getTitle()).isEqualTo(title),
                () -> assertThat(post.getContent()).isEqualTo(content),
                () -> assertThat(post.getPostImageUrl()).isEqualTo(postImageUrl)
        );

    }

    @Test
    void 게시글_업데이트시_사진_삭제에_성공한다() {
        // given
        Long postId = 1L;
        String title = "title test";
        String content = "content test";
        boolean imageExist = false;
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(title, content, imageExist);
        String postImageUrl = null;

        // when
        postService.updateClubBoardPost(postId, postUpdateRequest, postImageUrl);
        Post post = postRepository.findById(postId).orElseThrow();

        // then
        assertAll(
                () -> assertThat(post.getTitle()).isEqualTo(title),
                () -> assertThat(post.getContent()).isEqualTo(content),
                () -> assertThat(post.getPostImageUrl()).isNull()
        );
    }

    @Test
    void 게시글_제목과_내용_수정에_성공한다() {
        // given
        Long postId = 1L;
        String title = "title test";
        String content = "content test";
        boolean imageExist = true;
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(title, content, imageExist);
        String postImageUrl = null;

        // when
        postService.updateClubBoardPost(postId, postUpdateRequest, postImageUrl);
        Post post = postRepository.findById(postId).orElseThrow();

        // then
        assertAll(
                () -> assertThat(post.getTitle()).isEqualTo(title),
                () -> assertThat(post.getContent()).isEqualTo(content),
                () -> assertThat(post.getPostImageUrl()).isEqualTo("imageUrl1")
        );
    }

    @Test
    void 게시글_삭제에_성공한다() {
        // given
        Long postId = 2L;

        // when
        postService.deleteClubBoardPost(postId);

        // then
        assertThatThrownBy(() -> postRepository.findById(postId)
                .orElseThrow(IllegalArgumentException::new))
                .isInstanceOf(IllegalArgumentException.class);

    }

}
