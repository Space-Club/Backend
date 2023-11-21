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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Club extends BaseTimeEntity {

    private static final String CLUB_LOGO_S3_URL = "https://space-club-image-bucket.s3.ap-northeast-2.amazonaws.com/club-logo/";

    private static final String CLUB_COVER_S3_URL = "https://space-club-image-bucket.s3.ap-northeast-2.amazonaws.com/club-cover/";

    @Id
    @Column(name = "club_id")
    @Getter
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 12, nullable = false)
    @Getter
    private String name;

    @Lob
    private String logoImageName;

    @Lob
    private String coverImageName;

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
    public Club(Long id,
                String name,
                String logoImageName,
                String info,
                String coverImageName,
                List<ClubNotice> notices,
                List<ClubUser> clubUser,
                LocalDateTime createdAt,
                boolean isUpdate) {
        if (!isUpdate) {
            Assert.notNull(name, "이름에 null 값이 올 수 없습니다");
            Assert.hasText(name, "이름이 빈 값일 수 없습니다");
            Assert.isTrue(validateNameLength(name), "이름의 길이는 12글자를 넘을 수 없습니다");
            Assert.isTrue(name.equals(name.trim()), "이름의 맨앞과 맨뒤에는 공백이 추가될 수 없습니다");
        }

        this.id = id;
        this.name = name;
        this.logoImageName = logoImageName;
        this.coverImageName = coverImageName;
        this.info = info;
        this.createdAt = createdAt;

        if (notices != null) {
            this.notices = new ArrayList<>(notices);
        }
        if (clubUser != null) {
            this.clubUser = new ArrayList<>(clubUser);
        }
    }

    public Club update(Club newClub) {
        return Club.builder()
                .id(this.id)
                .name(newClub.name != null ? newClub.name : this.name)
                .info(newClub.info != null ? newClub.info : this.info)
                .logoImageName(this.logoImageName)
                .coverImageName(this.coverImageName)
                .notices(this.notices != null ? this.notices : null)
                .clubUser(this.clubUser != null ? this.clubUser : null)
                .createdAt(this.createdAt)
                .build();
    }

    public Club updateLogoImage(String logoImageName) {
        return Club.builder()
                .id(this.id)
                .name(this.name)
                .logoImageName(logoImageName)
                .coverImageName(this.coverImageName)
                .info(this.info)
                .notices(this.notices != null ? this.notices : null)
                .clubUser(this.clubUser != null ? this.clubUser : null)
                .createdAt(this.createdAt)
                .build();
    }

    public Club updateCoverImage(String coverImageName) {
        return Club.builder()
                .id(this.id)
                .name(this.name)
                .logoImageName(this.logoImageName)
                .coverImageName(coverImageName)
                .info(this.info)
                .notices(this.notices != null ? this.notices : null)
                .clubUser(this.clubUser != null ? this.clubUser : null)
                .createdAt(this.createdAt)
                .build();
    }

    public String getLogoImageName() {
        return CLUB_LOGO_S3_URL + this.logoImageName;
    }

    public String getCoverImageName() {
        return CLUB_COVER_S3_URL + this.coverImageName;
    }

}
