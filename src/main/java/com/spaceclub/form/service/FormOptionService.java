package com.spaceclub.form.service;

import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormOptionUser;
import com.spaceclub.form.repository.FormOptionRepository;
import com.spaceclub.form.repository.FormOptionUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spaceclub.global.ExceptionCode.FORM_OPTION_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class FormOptionService {

    private final FormOptionUserRepository formOptionUserRepository;

    private final FormOptionRepository formOptionRepository;

    public void createFormOption(Long userId, FormOptionUser formOptionUser) {
        FormOption formOption = formOptionRepository.findById(formOptionUser.getFormOptionId())
                .orElseThrow(() -> new IllegalStateException(FORM_OPTION_NOT_FOUND.toString()));

        FormOptionUser registeredFormOptionUser = formOptionUser.registerFormOptionAndUser(formOption, userId);
        formOptionUserRepository.save(registeredFormOptionUser);

        formOption.addFormOptionUser(registeredFormOptionUser);
        formOptionRepository.save(formOption);
    }

}
