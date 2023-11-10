package com.spaceclub.form.service;

import com.spaceclub.form.controller.dto.FormApplicationGetResponse;
import com.spaceclub.form.controller.dto.FormGetResponse;
import com.spaceclub.form.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepository;


    @Transactional
    public Long createForm() {
        return 1L;
    }

    public FormGetResponse getForm() {
        return FormGetResponse.builder().build();
    }

    @Transactional
    public void createApplicationForm() {
    }

    public List<FormApplicationGetResponse> getAllForms() {
        return List.of(FormApplicationGetResponse.builder().build());
    }


}
