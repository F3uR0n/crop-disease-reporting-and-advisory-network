package com.cdran.model.entity;

import com.cdran.model.enums.Language;
import com.cdran.model.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Field officer: verifies on-ground reports in high-risk areas. Accounts are created by an admin.
 * TODO (Sprint 4, Feature 20): add @ManyToMany Set&lt;Region&gt; coveredRegions.
 */
@Entity
@Table(name = "field_officers")
@DiscriminatorValue("FIELD_OFFICER")
public class FieldOfficer extends User {

    @Column(nullable = false, length = 30)
    private String employeeId;

    protected FieldOfficer() {
    }

    public FieldOfficer(String fullName, String phone, String email, String passwordHash, Language language,
                        String employeeId) {
        super(fullName, phone, email, passwordHash, language);
        this.employeeId = employeeId;
    }

    @Override
    public Role getRole() {
        return Role.FIELD_OFFICER;
    }

    public String getEmployeeId() {
        return employeeId;
    }
}
