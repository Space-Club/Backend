package com.spaceclub.user.service;

import com.spaceclub.global.S3ImageUploader;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import com.spaceclub.user.service.vo.UserProfile;
import com.spaceclub.user.service.vo.RequiredProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.spaceclub.global.ExceptionCode.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3ImageUploader imageUploader;

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

    public UserProfile getProfile(Long userId) {
        User user = getUser(userId);

        return UserProfile.of(user);
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

}
