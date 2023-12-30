package com.spaceclub.notification.mail.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Template {

    @Id
    @Column(name = "template_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Lob
    private String template;

    @Builder
    public Template(Long id, String template) {
        this.id = id;
        this.template = template;
    }

}
