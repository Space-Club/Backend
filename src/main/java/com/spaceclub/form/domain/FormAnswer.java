package com.spaceclub.form.domain;

import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class FormAnswer extends BaseTimeEntity {

    @Id
    @Column(name = "form_option_user_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Getter
    @ManyToOne
    @JoinColumn(name = "form_option_id")
    private FormOption formOption;

    @Getter
    private Long userId;

    @Getter
    private String content;

    @Builder
    public FormAnswer(Long id, FormOption formOption, Long userId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.formOption = formOption;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public FormAnswer registerFormOptionAndUser(FormOption formOption, Long userId) {
        return FormAnswer.builder()
                .id(this.id)
                .formOption(formOption)
                .userId(userId)
                .content(this.content)
                .createdAt(this.createdAt)
                .build();
    }

    public String getOptionTitle() {
        return formOption.getTitle();
    }

    public Long getFormOptionId() {
        return formOption.getId();
    }

}
