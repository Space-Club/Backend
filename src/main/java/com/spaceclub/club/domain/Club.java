package com.spaceclub.club.domain;

import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Club extends BaseTimeEntity {

    @Id
    @Column(name = "club_id")
    @GeneratedValue
    private Long id;

    @Column(length = 12, nullable = false)
    private String name;

    @Lob
    private String image;

    @Lob
    private String info;

    private String owner;

    private boolean validateNameLength() {
        return this.name.length() <= 12;
    }

    @Builder
    public Club(String name, String image, String info, String owner) {
        Assert.notNull(name, "이름에 null 값이 올 수 없습니다");
        Assert.hasText(name, "이름이 빈 값일 수 없습니다");
        Assert.isTrue(validateNameLength(), "이름의 길이는 12자를 넘을 수 없습니다");

        this.name = name;
        this.image = image;
        this.info = info;
        this.owner = owner;
    }

}
