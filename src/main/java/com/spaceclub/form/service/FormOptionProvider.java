package com.spaceclub.form.service;

import com.spaceclub.form.domain.FormAnswer;
import com.spaceclub.form.service.vo.FormUserInfo;

import java.util.List;

public interface FormOptionProvider {

    FormUserInfo createFormOption(Long userId, FormAnswer formAnswer);

    void deleteFormAnswer(List<FormAnswer> formAnswerInfos, Long userId);

}
