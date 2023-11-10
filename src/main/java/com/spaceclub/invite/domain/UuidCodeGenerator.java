package com.spaceclub.invite.domain;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidCodeGenerator implements InviteCodeGenerator {

    @Override
    public String generateCode() {
        return UUID.randomUUID().toString();
    }

}
