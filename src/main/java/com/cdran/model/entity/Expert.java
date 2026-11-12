package com.cdran.model.entity;

import com.cdran.model.enums.AccountStatus;
import com.cdran.model.enums.Language;
import com.cdran.model.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Expert (agronomist). Starts as PENDING_APPROVAL and cannot log in until an admin approves.
 * TODO (Sprint 2, Feature 9): add @ManyToMany Set&lt;Crop&gt; specialties and currentLoad()/canTakeCase(Crop).
 */
@Entity
@Table(name = "experts")
@DiscriminatorValue("EXPERT")
public class Expert extends User {

    @Column(nullable = false, length = 150)
    private String qualification;

    @Column(length = 150)
    private String institution;

    /** Workload cap used by the assignment algorithm. */
    @Column(nullable = false)
    private int maxActiveCases = 10;

    private LocalDateTime approvedAt;

    protected Expert() {
    }

    public Expert(String fullName, String phone, String email, String passwordHash, Language language,
                  String qualification, String institution) {
        super(fullName, phone, email, passwordHash, language);
        this.qualification = qualification;
        this.institution = institution;
        setStatus(AccountStatus.PENDING_APPROVAL);
    }

    @Override
    public Role getRole() {
        return Role.EXPERT;
    }

    /** Domain rule lives in the Model: approval activates the account and stamps the time. */
    public void approve(LocalDateTime when) {
        setStatus(AccountStatus.ACTIVE);
        this.approvedAt = when;
    }

    public String getQualification() {
        return qualification;
    }

    public String getInstitution() {
        return institution;
    }

    public int getMaxActiveCases() {
        return maxActiveCases;
    }

    public void setMaxActiveCases(int maxActiveCases) {
        this.maxActiveCases = maxActiveCases;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
}
