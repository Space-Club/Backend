package com.spaceclub.form.service;

import com.spaceclub.form.domain.FormOptionUser;
import com.spaceclub.form.service.vo.FormUserInfo;

import java.util.List;

public interface FormOptionProvider {

    FormUserInfo createFormOption(Long userId, FormOptionUser formOptionUser);

    void deleteFormOptionUser(List<FormOptionUser> formOptionUserInfos, Long userId);

}
