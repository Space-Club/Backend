package com.spaceclub.club;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InvitationCodeGenerator {

    @Getter
    private static final String initValue = "EXPIRED";

    public String generateInvitationCode() {

        return UUID.randomUUID().toString();
    }

}
