package com.spaceclub.user.controller;

import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
import com.spaceclub.user.controller.dto.UserProfileImageResponse;
import com.spaceclub.user.controller.dto.UserProfileResponse;
import com.spaceclub.user.controller.dto.UserProfileUpdateRequest;
import com.spaceclub.user.service.UserService;
import com.spaceclub.user.service.vo.RequiredProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping(value = {"/api/v1/me/profile", "/api/v1/users"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = {"", "/profiles"})
    public UserProfileResponse getProfile(@Authenticated JwtUser jwtUser) {
        return userService.getProfile(jwtUser.id()).toResponse();
    }

    @PutMapping
    @ResponseStatus(NO_CONTENT)
    public void updateProfile(@RequestBody UserProfileUpdateRequest request, @Authenticated JwtUser jwtUser) {
        RequiredProfile requiredProfile = new RequiredProfile(request.name(), request.phoneNumber());

        userService.updateRequiredProfile(jwtUser.id(), requiredProfile);
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void changeProfileImage(
            @RequestPart MultipartFile userImage,
            @Authenticated JwtUser jwtUser
    ) {
        userService.changeUserProfileImage(userImage, jwtUser.id());
    }

    @Deprecated
    @GetMapping("/images")
    public ResponseEntity<UserProfileImageResponse> getProfileImage(@Authenticated JwtUser jwtUser) {
        String response = userService.getProfile(jwtUser.id()).profileImageUrl();

        return ResponseEntity.ok().body(new UserProfileImageResponse(response));
    }

    @Deprecated
    @PatchMapping("/required-infos")
    @ResponseStatus(NO_CONTENT)
    public void updateProfile_deprecated(@RequestBody UserProfileUpdateRequest request, @Authenticated JwtUser jwtUser) {
        RequiredProfile requiredProfile = new RequiredProfile(request.name(), request.phoneNumber());

        userService.updateRequiredProfile(jwtUser.id(), requiredProfile);
    }

}
