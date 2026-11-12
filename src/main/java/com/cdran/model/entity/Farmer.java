package com.cdran.model.entity;

import com.cdran.model.enums.Language;
import com.cdran.model.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Farmer: reports cases, manages fields, tracks cases, gets alerts and advice.
 * TODO (Sprint 1, Feature 5): add @ManyToOne Region and @OneToMany List&lt;Field&gt; once Region/Field entities exist.
 */
@Entity
@Table(name = "farmers")
@DiscriminatorValue("FARMER")
public class Farmer extends User {

    @Column(length = 100)
    private String village;

    protected Farmer() {
    }

    public Farmer(String fullName, String phone, String email, String passwordHash, Language language) {
        super(fullName, phone, email, passwordHash, language);
    }

    @Override
    public Role getRole() {
        return Role.FARMER;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }
}
