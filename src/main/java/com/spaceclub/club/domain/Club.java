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

    private static final String CLUB_LOGO_S3_URL = "https://space-club-image-bucket.s3.ap-northeast-2.amazonaws.com/club-logo/";

    private static final String INVITE_FIXED_URL = "https://spaceclub.site/api/v1/clubs/invite/";

    @Id
    @Column(name = "club_id")
    @Getter
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 12, nullable = false)
    @Getter
    private String name;

    @Lob
    private String logoImageUrl;

    @Lob
    @Getter
    private String coverImageUrl;

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
    public Club(Long id, String name, String logoImageUrl, String info, String owner, Invitation invitation, String coverImageUrl, List<ClubNotice> notices) {
        Assert.notNull(name, "이름에 null 값이 올 수 없습니다");
        Assert.hasText(name, "이름이 빈 값일 수 없습니다");
        Assert.isTrue(validateNameLength(name), "이름의 길이는 12글자를 넘을 수 없습니다");
        Assert.isTrue(name.equals(name.trim()), "이름의 맨앞과 맨뒤에는 공백이 추가될 수 없습니다");

        this.id = id;
        this.name = name;
        this.logoImageUrl = logoImageUrl;
        this.coverImageUrl = coverImageUrl;
        this.info = info;
        this.owner = owner;
        this.invitation = invitation;

        if (notices != null) {
            this.notices = new ArrayList<>(notices);
        }
    }

    public Club assignInvitationCode(String invitationCode) {
        this.invitation = Invitation.builder()
                .invitationCode(invitationCode)
                .invitationCodeGeneratedAt(LocalDateTime.now())
                .build();

        return this;
    }

    public String getInvitationCode() {
        return invitation.getInvitationCode();
    }

    public String getInviteUrl() {
        if (invitation.getInvitationCode() == null) {
            return null;
        }
        return INVITE_FIXED_URL + invitation.getInvitationCode();
    }

    public String getLogoImageUrl() {
        if (logoImageUrl == null) {
            return null;
        }
        return CLUB_LOGO_S3_URL + logoImageUrl;
    }

}
