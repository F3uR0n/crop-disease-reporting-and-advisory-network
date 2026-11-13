package com.cdran.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** Master data: one item of the symptom checklist on the case form. Seeded by Flyway. */
@Entity
@Table(name = "symptoms")
public class Symptom extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nameEn;

    @Column(nullable = false, length = 100)
    private String nameBn;

    protected Symptom() {
    }

    public Symptom(String nameEn, String nameBn) {
        this.nameEn = nameEn;
        this.nameBn = nameBn;
    }

    /** Name in the page language. Views call it as symptom.name(#locale.language). */
    public String name(String language) {
        return "bn".equals(language) ? nameBn : nameEn;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameBn() {
        return nameBn;
    }
}
