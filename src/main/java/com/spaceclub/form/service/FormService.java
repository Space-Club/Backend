package com.spaceclub.form.service;

import com.spaceclub.form.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepository;


    @Transactional
    public Long createForm() {
        return 1L;
    }

}
