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

    @Builder
    public Club(Long id, String name, String image, String info) {
        Assert.notNull(id, "클럽 ID는 null 값이 올 수 없습니다");
        Assert.hasText(Long.toString(id), "클럽 ID는 빈 값이 올 수 없습니다");
        Assert.notNull(name, "이름에 null 값이 올 수 없습니다");
        Assert.hasText(name, "이름이 빈 값일 수 없습니다");

        this.id = id;
        this.name = name;
        this.image = image;
        this.info = info;
    }

}
