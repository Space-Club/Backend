package com.spaceclub.board.service;

import com.spaceclub.board.controller.domain.Post;
import com.spaceclub.board.controller.dto.PostRequest;
import com.spaceclub.board.controller.dto.PostUpdateRequest;
import com.spaceclub.board.repository.PostRepository;
import com.spaceclub.board.service.vo.PostUpdateCommand;
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

    public Page<Post> getClubBoardPostsByPaging(Pageable pageable, Long clubId, Long userId) {
        return postRepository.findByClubId(clubId, pageable);
    }

    public Post getClubBoardPost(Long clubId, Long postId, Long userId) {
        return postRepository.findByClubIdAndId(clubId, postId).orElseThrow();
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
    public void updateClubBoardPost(Long postId, PostUpdateRequest postRequest, Long userId, String postImageUrl) {
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
    public void deleteClubBoardPost(Long postId, Long userId) {
        postRepository.deleteById(postId);
    }

}
