package com.cdran.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** Master data: a crop a farmer can report a case for (rice, potato, ...). Seeded by Flyway. */
@Entity
@Table(name = "crops")
public class Crop extends BaseEntity {

    @Column(nullable = false, length = 60)
    private String nameEn;

    @Column(nullable = false, length = 60)
    private String nameBn;

    protected Crop() {
    }

    public Crop(String nameEn, String nameBn) {
        this.nameEn = nameEn;
        this.nameBn = nameBn;
    }

    /** Name in the page language. Views call it as crop.name(#locale.language). */
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
