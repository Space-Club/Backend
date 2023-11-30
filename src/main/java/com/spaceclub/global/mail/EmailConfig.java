package com.spaceclub.global.mail;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackages = {"com.spaceclub.global.mail"})
public class EmailConfig {

}
