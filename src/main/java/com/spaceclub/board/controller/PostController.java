package com.spaceclub.board.controller;

import com.spaceclub.board.controller.dto.PostRequest;
import com.spaceclub.board.controller.dto.PostResponse;
import com.spaceclub.board.controller.dto.PostUpdateRequest;
import com.spaceclub.board.service.PostService;
import com.spaceclub.board.service.vo.PostInfo;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.vo.JwtUser;
import com.spaceclub.global.s3.S3Folder;
import com.spaceclub.global.s3.S3ImageUploader;
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

    private final PostService postService;
    private final S3ImageUploader imageUploader;

    @GetMapping("/{clubId}")
    public PageResponse<PostResponse, PostInfo> getClubBoardPostsByPaging(
            @PageableDefault(sort = "id", direction = DESC) Pageable pageable,
            @PathVariable Long clubId
    ) {
        Page<PostInfo> postPages = postService.getClubBoardPostsByPaging(pageable, clubId);
        List<PostResponse> posts = postPages.getContent().stream()
                .map(post -> PostResponse.from(post, imageUploader.getBucketUrl()))
                .toList();

        return new PageResponse<>(posts, postPages);
    }

    @GetMapping("/{clubId}/{postId}")
    public PostResponse getSingleClubBoardPost(
            @PathVariable Long clubId,
            @PathVariable Long postId
    ) {
        PostInfo postInfo = postService.getClubBoardPost(clubId, postId);

        return PostResponse.from(postInfo, imageUploader.getBucketUrl());
    }

    @PostMapping(value = "/{clubId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createClubBoardPost(
            @RequestPart(required = false) MultipartFile multipartFile,
            @RequestPart PostRequest postRequest,
            @PathVariable Long clubId,
            @Authenticated JwtUser jwtUser) {
        Long userId = jwtUser.id();

        // 이미지가 존재할 경우
        if (multipartFile != null) {
            String postImageUrl = imageUploader.upload(multipartFile, S3Folder.POST_IMAGE);
            Long postId = postService.createClubBoardPost(clubId, postRequest, userId, postImageUrl);

            return ResponseEntity
                    .status(CREATED)
                    .location(URI.create("/api/v1/boards/posts/%d/%d".formatted(clubId, postId)))
                    .build();
        }
        // 이미지가 존재하지 않을 경우
        Long postId = postService.createClubBoardPost(clubId, postRequest, userId, null);

        return ResponseEntity
                .status(CREATED)
                .location(URI.create("/api/v1/boards/posts/%d/%d".formatted(clubId, postId)))
                .build();
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateClubBoardPost(
            @RequestPart(required = false) MultipartFile multipartFile,
            @RequestPart PostUpdateRequest postRequest,
            @PathVariable Long postId
    ) {
        if (multipartFile != null) {
            String postImageUrl = imageUploader.upload(multipartFile, S3Folder.POST_IMAGE);
            postService.updateClubBoardPost(postId, postRequest, postImageUrl);
            return;
        }
        postService.updateClubBoardPost(postId, postRequest, null);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(code = NO_CONTENT)
    public void deleteClubBoardPost(@PathVariable Long postId) {
        postService.deleteClubBoardPost(postId);
    }

}

