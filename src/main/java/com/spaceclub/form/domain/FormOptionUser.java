package com.spaceclub.form.domain;

import com.spaceclub.global.BaseTimeEntity;
import com.spaceclub.user.domain.User;
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
public class FormOptionUser extends BaseTimeEntity {

    @Id
    @Column(name = "form_option_user_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Getter
    @ManyToOne
    @JoinColumn(name = "form_option_id")
    private FormOption formOption;

    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    private String content;

    @Builder
    public FormOptionUser(Long id, FormOption formOption, User user, String content, LocalDateTime createdAt) {
        this.id = id;
        this.formOption = formOption;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
    }

    public FormOptionUser registerFormOptionAndUser(FormOption formOption, User user) {
        return FormOptionUser.builder()
                .id(this.id)
                .formOption(formOption)
                .user(user)
                .content(this.content)
                .createdAt(this.createdAt)
                .build();
    }

    public Long getUserId() {
        return user.getId();
    }

    public String getOptionTitle() {
        return formOption.getTitle();
    }

    public Long getFormOptionId() {
        return formOption.getId();
    }

}
