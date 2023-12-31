package com.spaceclub.user.service;

import com.spaceclub.global.s3.S3Folder;
import com.spaceclub.global.s3.S3ImageUploader;
import com.spaceclub.global.config.s3.S3Properties;
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

    private final S3Properties s3Properties;

    @Transactional
    public void changeUserProfileImage(MultipartFile userImage, Long userId) {
        String profileUrl = imageUploader.upload(userImage, S3Folder.USER_PROFILE);
        User user = getUser(userId);

        userRepository.save(user.changeProfileImageUrl(profileUrl));
    }

    @Transactional
    public void updateRequiredProfile(Long userId, RequiredProfile userRequiredInfo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND.toString()))
                .updateRequiredInfo(userRequiredInfo.name(), userRequiredInfo.phoneNumber(), userRequiredInfo.email());

        userRepository.save(user);
    }

    @Override
    public UserProfile getProfile(Long userId) {
        User user = getUser(userId);

        return UserProfile.of(user, s3Properties.url());
    }

    @Override
    public void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) throw new IllegalArgumentException(USER_NOT_FOUND.toString());
    }

    @Override
    public String getEmailAddress(Long userId) {
        return userRepository.findEmail(userId).orElseThrow(() -> new IllegalStateException("유저가 동의하지 않습니다."));
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND.toString()));
    }

    @Transactional
    public void removeUserProfileImage(Long id) {
        User user = getUser(id);

        userRepository.save(user.removeProfileImageUrl());
    }

}
