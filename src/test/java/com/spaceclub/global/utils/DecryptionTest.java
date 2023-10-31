package com.spaceclub.global.utils;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Disabled
@SpringBootTest
public class DecryptionTest {

    private static final String ENCRYPTED_MESSAGE = "";
    private static final String PASSWORD = "";

    @Configuration
    static class Config {

        @Bean("encryptor")
        public StringEncryptor stringEncryptor() {
            SimpleStringPBEConfig config = new SimpleStringPBEConfig();
            config.setPassword(PASSWORD);             //암호화, 복호화할 때 사용할 패스워드
            config.setAlgorithm("PBEWithMD5AndDES");  //암호화 알고리즘
            config.setKeyObtentionIterations("1000"); //해시 반복 횟수
            config.setPoolSize("1");

            PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            encryptor.setConfig(config);

            return encryptor;
        }

    }

    @Autowired
    private StringEncryptor encryptor;

    @Test
    public void decrypt() {
        String decrypt = encryptor.decrypt(ENCRYPTED_MESSAGE);
        System.out.println(decrypt);
    }

}
