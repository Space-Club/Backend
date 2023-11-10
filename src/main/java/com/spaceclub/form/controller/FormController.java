package com.spaceclub.form.controller;

import com.spaceclub.form.service.FormService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

}
