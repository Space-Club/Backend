package com.spaceclub.global.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"local", "develop"})
@Configuration
@ConfigurationPropertiesScan(basePackages = {"com.spaceclub.global.config"})
public class PropertiesConfig {

    @Value("${jasypt.encryptor.password}")
    private String password;

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);             //암호화, 복호화할 때 사용할 패스워드
        config.setAlgorithm("PBEWithMD5AndDES");  //암호화 알고리즘
        config.setKeyObtentionIterations("1000"); //해시 반복 횟수
        config.setPoolSize("1");

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(config);

        return encryptor;
    }

}
