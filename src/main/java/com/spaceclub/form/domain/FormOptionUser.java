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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class FormOptionUser extends BaseTimeEntity {

    @Id
    @Column(name = "form_item_user_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "form_item_id")
    private FormOption formOption;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

}
