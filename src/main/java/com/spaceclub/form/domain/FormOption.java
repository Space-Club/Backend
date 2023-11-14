package com.spaceclub.form.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class FormOption {

    @Id
    @Getter
    @Column(name = "form_option_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Getter
    private String title;

    @Getter
    @Enumerated(EnumType.STRING)
    private FormOptionType type;

    private boolean visible;

    @ManyToOne
    @JoinColumn(name = "form_id")
    private Form form;

    @Getter
    @OneToMany(mappedBy = "formOption", fetch = FetchType.EAGER, cascade = {REMOVE})
    private final List<FormOptionUser> optionUsers = new ArrayList<>();

    @Builder
    public FormOption(Long id, String title, FormOptionType type, boolean visible, Form form) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.visible = visible;
        this.form = form;
    }

    public FormOption registerForm(Form form) {
        return FormOption.builder()
                .id(id)
                .title(title)
                .type(type)
                .visible(visible)
                .form(form)
                .build();
    }

}
