package com.cdran.model.entity;

import com.cdran.model.enums.AccountStatus;
import com.cdran.model.enums.Language;
import com.cdran.model.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Base account for every actor. Inheritance strategy JOINED:
 * shared columns live in "users", role-specific columns in farmers / experts / field_officers / admins.
 * The "role" column tells Hibernate which subclass a row belongs to.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "role")
public abstract class User extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String fullName;

    /** Login id. Farmers are low-tech users, so login is by phone number, not email. */
    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    // Convention for ALL enum columns: STRING + VARCHAR. Without @JdbcTypeCode, Hibernate expects a native
    // MySQL ENUM column and schema validation fails against our VARCHAR Flyway columns.
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 5)
    private Language preferredLanguage = Language.EN;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;

    protected User() {
        // for JPA
    }

    protected User(String fullName, String phone, String email, String passwordHash, Language preferredLanguage) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.passwordHash = passwordHash;
        this.preferredLanguage = preferredLanguage;
    }

    /** Each subclass states its role; used for Spring Security authorities and view selection. */
    public abstract Role getRole();

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Language getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(Language preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
