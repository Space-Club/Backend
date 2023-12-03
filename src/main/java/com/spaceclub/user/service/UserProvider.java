package com.spaceclub.user.service;

import com.spaceclub.user.service.vo.UserProfile;

public interface UserProvider {

    UserProfile getProfile(Long userId);

    void validateUser(Long userId);

}
