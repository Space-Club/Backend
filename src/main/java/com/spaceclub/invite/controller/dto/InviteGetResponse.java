package com.spaceclub.invite.controller.dto;

public record InviteGetResponse(
        String inviteLink,
        Boolean isExpired
) {

}
