package com.cdran.model.entity;

import com.cdran.model.enums.Language;
import com.cdran.model.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** Admin: approves experts, manages users, handles flagged content and system settings. */
@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    @Column(length = 100)
    private String designation;

    protected Admin() {
    }

    public Admin(String fullName, String phone, String email, String passwordHash, Language language) {
        super(fullName, phone, email, passwordHash, language);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
