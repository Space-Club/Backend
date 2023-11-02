package com.spaceclub.club.domain;

import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Club extends BaseTimeEntity {

    @Id
    @Column(name = "club_id")
    @Getter
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 12, nullable = false)
    @Getter
    private String name;

    @Lob
    @Getter
    private String thumbnailUrl;

    @Lob
    @Getter
    private String info;

    @Getter
    private String owner;

    @Embedded
    private Invitation invitation;

    @Getter
    @OneToMany(mappedBy = "club", fetch = FetchType.EAGER)
    private List<ClubNotice> notices = new ArrayList<>();

    @Getter
    @OneToMany(mappedBy = "club", fetch = FetchType.EAGER)
    private List<ClubUser> clubUser = new ArrayList<>();

    private boolean validateNameLength(String name) {
        return name.length() <= 12;
    }

    @Builder
    public Club(Long id, String name, String thumbnailUrl, String info, String owner, Invitation invitation, List<ClubNotice> notices) {
        Assert.notNull(name, "이름에 null 값이 올 수 없습니다");
        Assert.hasText(name, "이름이 빈 값일 수 없습니다");
        Assert.isTrue(validateNameLength(name), "이름의 길이는 12글자를 넘을 수 없습니다");
        Assert.isTrue(name.equals(name.trim()), "이름의 맨앞과 맨뒤에는 공백이 추가될 수 없습니다");

        this.id = id;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.info = info;
        this.owner = owner;
        this.invitation = invitation;

        if (notices != null) {
            this.notices = new ArrayList<>(notices);
        }
    }

    public String getInvitationCode() {
        return invitation.getInvitationCode();
    }

    public Club assignInvitationCode(String invitationCode) {
        this.invitation = Invitation.builder()
                .invitationCode(invitationCode)
                .invitationCodeGeneratedAt(LocalDateTime.now())
                .build();

        return this;
    }

}
