package com.spaceclub.invitation;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class InvitationCodeGenerator {

    private static final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String generateInvitationCode() {
        StringBuilder code = new StringBuilder(6);
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }

}
