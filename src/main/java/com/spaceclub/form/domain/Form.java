package com.spaceclub.form.domain;

import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Form extends BaseTimeEntity {

    @Id
    @Getter
    @Column(name = "form_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Getter
    @Column(length = 1000)
    private String description;

    @Getter
    private boolean managed;

    @Getter
    @OneToMany(mappedBy = "form", cascade = {PERSIST, REMOVE}, fetch = FetchType.EAGER)
    private final List<FormOption> options = new ArrayList<>();

    @Builder
    public Form(Long id, String description, boolean managed) {
        this.id = id;
        this.description = description;
        this.managed = managed;
    }

    public void addItems(List<FormOption> options) {
        for (FormOption item : options) {
            addItem(item);
        }
    }

    public void addItem(FormOption option) {
        FormOption formOption = option.registerForm(this);
        options.add(formOption);
    }

}
