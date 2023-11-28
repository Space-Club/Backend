package com.spaceclub.invite.service.util;

import java.util.UUID;

public class UuidCodeGenerator implements InviteCodeGenerator {

    @Override
    public String generateCode() {
        return UUID.randomUUID().toString();
    }

}
