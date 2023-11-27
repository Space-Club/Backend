package com.spaceclub.user.service;

import com.spaceclub.global.config.s3.S3ImageUploader;
import com.spaceclub.global.config.s3.properties.S3BucketUrl;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import com.spaceclub.user.service.vo.RequiredProfile;
import com.spaceclub.user.service.vo.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.spaceclub.user.UserExceptionMessage.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserProvider {

    private final UserRepository userRepository;

    private final S3ImageUploader imageUploader;

    private final S3BucketUrl s3BucketUrl;

    @Transactional
    public void changeUserProfileImage(MultipartFile userImage, Long userId) {
        String profileUrl = imageUploader.uploadUserProfileImage(userImage);
        changeProfileImageUrl(userId, profileUrl);
    }

    @Transactional
    public void updateRequiredProfile(Long userId, RequiredProfile userRequiredInfo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND.toString()))
                .updateRequiredInfo(userRequiredInfo.name(), userRequiredInfo.phoneNumber());

        userRepository.save(user);
    }

    @Override
    public UserProfile getProfile(Long userId) {
        User user = getUser(userId);

        return UserProfile.of(user, s3BucketUrl.userProfileImage());
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND.toString()));
    }

    @Transactional
    public void changeProfileImageUrl(Long userId, String profileUrl) {
        User user = getUser(userId);

        userRepository.save(user.changeProfileImageUrl(profileUrl));
    }

    @Transactional
    public void removeUserProfileImage(Long id) {
        User user = getUser(id);

        userRepository.save(user.removeProfileImageUrl());
    }

}
