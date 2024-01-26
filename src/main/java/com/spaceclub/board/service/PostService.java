package com.spaceclub.board.service;

import com.spaceclub.board.controller.domain.Post;
import com.spaceclub.board.controller.dto.PostRequest;
import com.spaceclub.board.controller.dto.PostUpdateRequest;
import com.spaceclub.board.repository.PostRepository;
import com.spaceclub.board.service.vo.PostInfo;
import com.spaceclub.board.service.vo.PostUpdateCommand;
import com.spaceclub.user.service.UserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserProvider userProvider;

    public Page<PostInfo> getClubBoardPostsByPaging(Pageable pageable, Long clubId) {
        return postRepository.findByClubId(clubId, pageable)
                .map(post -> PostInfo.of(post, userProvider.getProfile(post.getAuthorId())));
    }

    public PostInfo getClubBoardPost(Long clubId, Long postId) {
        Post post = postRepository.findByClubIdAndId(clubId, postId).orElseThrow();

        return PostInfo.of(post, userProvider.getProfile(post.getAuthorId()));
    }

    @Transactional
    public Long createClubBoardPost(Long clubId, PostRequest postRequest, Long userId, String postImageUrl) {
        Post post = Post.builder()
                .title(postRequest.title())
                .content(postRequest.content())
                .postImageUrl(postImageUrl)
                .authorId(userId)
                .clubId(clubId)
                .build();

        return postRepository.save(post).getId();
    }

    @Transactional
    public void updateClubBoardPost(Long postId, PostUpdateRequest postRequest, String postImageUrl) {
        boolean doesImageExists = postRequest.doesPostImageExist();

        switch (PostUpdateCommand.getCommand(doesImageExists, postImageUrl)) {
            case UPDATE_ONLY_CONTENT -> postRepository.findById(postId).orElseThrow()
                    .updatePost(postRequest.title(), postRequest.content());

            case UPDATE_CONTENT_AND_IMAGE -> postRepository.findById(postId).orElseThrow()
                    .updatePost(postRequest.title(), postRequest.content(), postImageUrl);

            case UPDATE_CONTENT_AND_ERASE_IMAGE -> postRepository.findById(postId).orElseThrow()
                    .updatePost(postRequest.title(), postRequest.content(), null);
        }
    }

    @Transactional
    public void deleteClubBoardPost(Long postId) {
        postRepository.deleteById(postId);
    }

}
