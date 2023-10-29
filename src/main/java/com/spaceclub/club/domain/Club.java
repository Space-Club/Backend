package com.spaceclub.club.domain;

import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private String image;

    @Lob
    @Getter
    private String info;

    @Getter
    private String owner;

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
    public Club(Long id, String name, String image, String info, String owner, List<ClubNotice> notices) {
        Assert.notNull(name, "이름에 null 값이 올 수 없습니다");
        Assert.hasText(name, "이름이 빈 값일 수 없습니다");
        Assert.isTrue(validateNameLength(name), "이름의 길이는 12글자를 넘을 수 없습니다");
        Assert.isTrue(name.equals(name.trim()), "이름의 맨앞과 맨뒤에는 공백이 추가될 수 없습니다");

        this.id = id;
        this.name = name;
        this.image = image;
        this.info = info;
        this.owner = owner;

        if (notices != null) {
            this.notices = new ArrayList<>(notices);
        }
    }

}
