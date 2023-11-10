package com.spaceclub.form.domain;

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
@EqualsAndHashCode(of = "id")
public class FormItem {

    @Id
    @Column(name = "form_item_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "form_id")
    private Form form;

}
