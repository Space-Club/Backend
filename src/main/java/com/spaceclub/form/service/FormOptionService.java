package com.spaceclub.form.service;

import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormAnswer;
import com.spaceclub.form.repository.FormOptionRepository;
import com.spaceclub.form.repository.FormAnswerRepository;
import com.spaceclub.form.service.vo.FormUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spaceclub.form.FormExceptionMessage.FORM_OPTION_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class FormOptionService implements FormOptionProvider {

    private final FormAnswerRepository formAnswerRepository;

    private final FormOptionRepository formOptionRepository;

    @Override
    public FormUserInfo createFormOption(Long userId, FormAnswer formAnswer) {
        FormOption formOption = formOptionRepository.findById(formAnswer.getFormOptionId())
                .orElseThrow(() -> new IllegalStateException(FORM_OPTION_NOT_FOUND.toString()));

        FormAnswer registeredFormAnswer = formAnswer.registerFormOptionAndUser(formOption, userId);
        formAnswerRepository.save(registeredFormAnswer);

        formOption.addFormAnswer(registeredFormAnswer);
        formOptionRepository.save(formOption);

        return FormUserInfo.from(formOption, registeredFormAnswer);
    }

    @Override
    public void deleteFormAnswer(List<FormAnswer> formAnswerInfos, Long userId) {
        for (FormAnswer formAnswerInfo : formAnswerInfos) {
            Long formOptionId = formAnswerInfo.getFormOptionId();

            FormOption formOption = formOptionRepository.findById(formOptionId)
                    .orElseThrow(() -> new IllegalStateException(FORM_OPTION_NOT_FOUND.toString()));

            FormAnswer formAnswer = formAnswerRepository.findByFormOption_IdAndUserId(formAnswerInfo.getFormOptionId(), userId)
                    .orElseThrow(() -> new IllegalStateException(FORM_OPTION_NOT_FOUND.toString()));

            formOption.removeFormAnswer(formAnswer);
        }
    }

}
