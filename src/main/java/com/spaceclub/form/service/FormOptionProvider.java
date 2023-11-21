package com.spaceclub.form.service;

import com.spaceclub.form.domain.FormOptionUser;
import com.spaceclub.form.service.vo.FormUserInfo;

public interface FormOptionProvider {

    FormUserInfo createFormOption(Long userId, FormOptionUser formOptionUser);

}
