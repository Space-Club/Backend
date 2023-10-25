package com.spaceclub.global.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class EncryptionTest {

    @Test
    void jasypt를_통해_암호화에_성공한다() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

        encryptor.setPassword(""); // 암호화 및 복호화 시 사용하는 패스워드
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setStringOutputType("base64");

        String url = "";
        String username = "";
        String password = "";
        String urlResult = encryptor.encrypt(url);
        String usernameResult = encryptor.encrypt(username);
        String passwordResult = encryptor.encrypt(password);

        System.out.println("url plain : " + encryptor.decrypt(urlResult));          //복호화
        System.out.println("url encoding : " + urlResult);                          //암호화 (설정파일에 사용)

        System.out.println("username plain : " + encryptor.decrypt(usernameResult));//복호화
        System.out.println("username encoding : " + usernameResult);                //암호화 (설정파일에 사용)

        System.out.println("password plain : " + encryptor.decrypt(passwordResult));//복호화
        System.out.println("password encoding : " + passwordResult);                //암호화 (설정파일에 사용)
    }

}
