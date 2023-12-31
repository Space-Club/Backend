package com.spaceclub.board.controller;

import com.spaceclub.board.controller.domain.Post;
import com.spaceclub.board.controller.dto.PostRequest;
import com.spaceclub.board.controller.dto.PostResponse;
import com.spaceclub.board.service.BoardService;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.vo.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/boards/posts")
@RequiredArgsConstructor
public class PostController {

    private final BoardService boardService;

    @GetMapping("/{clubId}") //api/v1/boards/{clubId}?page={pageNum}&size={size}&sort=id,desc
    public PageResponse<PostResponse, Post> getClubBoardPostsByPaging(
            @PageableDefault(sort = "id", direction = DESC) Pageable pageable,
            @PathVariable Long clubId,
            @Authenticated JwtUser jwtUser) {
        // 게시글 페이징 조회
        Page<Post> postPages = boardService.getClubBoardPostsByPaging(pageable, clubId, jwtUser.id());
        List<PostResponse> posts = postPages.getContent().stream()
                .map(PostResponse::from)
                .toList();

        return new PageResponse<>(posts, postPages);
    }

    @GetMapping("/{clubId}/{postId}")
    public PostResponse getSingleClubBoardPost(
            @PathVariable Long clubId,
            @PathVariable Long postId,
            @Authenticated JwtUser jwtUser) {
        // 게시글 단건 조회
        Long userId = jwtUser.id();
        Post post = boardService.getClubBoardPost(clubId, postId, userId);

        return PostResponse.from(post);
    }

    @PostMapping(value = "/{clubId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createClubBoardPost(
            @RequestPart(required = false) MultipartFile multipartFile,
            @RequestPart PostRequest postRequest,
            @PathVariable Long clubId,
            @Authenticated JwtUser jwtUser) {
        // 게시글 생성
        Long userId = jwtUser.id();
        Long postId = boardService.createClubBoardPost(clubId, postRequest, userId);

        return ResponseEntity
                .status(CREATED)
                .location(URI.create("/api/v1/boards/posts/%d/%d".formatted(clubId, postId)))
                .build();
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateClubBoardPost(
            @RequestPart(required = false) MultipartFile multipartFile,
            @RequestPart PostRequest postRequest,
            @PathVariable Long postId,
            @Authenticated JwtUser jwtUser) {
        // 게시글 수정
        Long userId = jwtUser.id();
        boardService.updateClubBoardPost(postId, postRequest, userId);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(code = NO_CONTENT)
    public void deleteClubBoardPost(
            @PathVariable Long postId,
            @Authenticated JwtUser jwtUser) {
        // 게시글 삭제
        Long userId = jwtUser.id();
        boardService.deleteClubBoardPost(postId, userId);
    }

}

