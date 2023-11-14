package com.spaceclub.club.domain;

import com.spaceclub.global.BaseTimeEntity;
import com.spaceclub.invite.domain.Invite;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

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
    private String logoImageUrl;

    @Lob
    @Getter
    private String coverImageUrl;

    @Lob
    @Getter
    private String info;

    @Getter
    @OneToMany(
            mappedBy = "club",
            fetch = FetchType.EAGER,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<ClubNotice> notices = new ArrayList<>();

    @Getter
    @OneToMany(
            mappedBy = "club",
            fetch = FetchType.EAGER,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<ClubUser> clubUser = new ArrayList<>();

    @OneToOne(
            mappedBy = "club",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private Invite invite;

    private boolean validateNameLength(String name) {
        return name.length() <= 12;
    }

    @Builder
    public Club(Long id, String name, String logoImageUrl, String info, String coverImageUrl, List<ClubNotice> notices) {
        Assert.notNull(name, "이름에 null 값이 올 수 없습니다");
        Assert.hasText(name, "이름이 빈 값일 수 없습니다");
        Assert.isTrue(validateNameLength(name), "이름의 길이는 12글자를 넘을 수 없습니다");
        Assert.isTrue(name.equals(name.trim()), "이름의 맨앞과 맨뒤에는 공백이 추가될 수 없습니다");

        this.id = id;
        this.name = name;
        this.logoImageUrl = logoImageUrl;
        this.coverImageUrl = coverImageUrl;
        this.info = info;

        if (notices != null) {
            this.notices = new ArrayList<>(notices);
        }
    }

    private Club(Club club, String logoImageUrl) {
        this.id = club.getId();
        this.name = club.getName();
        this.info = club.getInfo();
        this.logoImageUrl = logoImageUrl;
        this.coverImageUrl = club.getCoverImageUrl();

        if (club.getNotices() != null) {
            this.notices = club.getNotices();
        }

        this.createdAt = club.getCreatedAt();
    }

    private Club(Club club, String name, String info) {
        this.id = club.getId();
        this.logoImageUrl = club.getLogoImageUrl();
        this.coverImageUrl = club.getCoverImageUrl();
        this.name = name;
        this.info = info;
        this.createdAt = club.getCreatedAt();
    }

    public Club update(Club newClub) {
        return new Club(this, newClub.getName(), newClub.getInfo());
    }

    public Club addLogoImageUrl(String logoImageUrl) {
        return new Club(this, logoImageUrl);
    }

}
