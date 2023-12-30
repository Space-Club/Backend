package com.spaceclub.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import static org.thymeleaf.templatemode.TemplateMode.HTML;

@Configuration
public class ThymeleafConfig {

    @Bean
    public StringTemplateResolver stringTemplateResolver() {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(HTML);

        return resolver;
    }

}
