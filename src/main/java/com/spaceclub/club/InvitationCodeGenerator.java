package com.spaceclub.club;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InvitationCodeGenerator {

    private static final String url = "https://spaceclub.site/api/v1/clubs/invite/";

    public String generateInvitationCode() {
        StringBuilder code = new StringBuilder(url);
        UUID uuid = UUID.randomUUID();

        return code.append(uuid).toString();
    }

}
