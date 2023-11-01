package com.spaceclub.invitation.service;

import com.spaceclub.invitation.InvitationCodeGenerator;
import com.spaceclub.invitation.domain.Invitation;
import com.spaceclub.invitation.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository repository;

    private final InvitationCodeGenerator generator;

    public String getCode(Long clubId) {
        Invitation invitation = repository.findByClubId(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다"));

        String code = generator.generateInvitationCode();
        Invitation newInvitation = invitation.assignCode(code);

        repository.save(newInvitation);
        return code;
    }

}
