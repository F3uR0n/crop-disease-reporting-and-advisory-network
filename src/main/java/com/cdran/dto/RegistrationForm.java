package com.cdran.dto;

import com.cdran.model.enums.Language;
import com.cdran.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Form-backing object (DTO) for the registration page. The View binds to this, never to the entity,
 * so users cannot set fields like status or role-specific flags through the form.
 * Validation messages are i18n keys resolved from messages_*.properties.
 */
public class RegistrationForm {

    @NotBlank(message = "{validation.required}")
    @Size(max = 100)
    private String fullName;

    /** Bangladeshi mobile number: 01[3-9] followed by 8 digits. */
    @NotBlank(message = "{validation.required}")
    @Pattern(regexp = "^01[3-9]\\d{8}$", message = "{validation.phone}")
    private String phone;

    @Email(message = "{validation.email}")
    @Size(max = 120)
    private String email;

    @NotBlank(message = "{validation.required}")
    @Size(min = 8, max = 64, message = "{validation.password.size}")
    private String password;

    @NotBlank(message = "{validation.required}")
    private String confirmPassword;

    /** Only FARMER or EXPERT may self-register; officers and admins are created by an admin. */
    @NotNull
    private Role accountType = Role.FARMER;

    @NotNull
    private Language preferredLanguage = Language.EN;

    // Expert-only fields (checked in RegistrationService when accountType == EXPERT)
    @Size(max = 150)
    private String qualification;

    @Size(max = 150)
    private String institution;

    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Role getAccountType() {
        return accountType;
    }

    public void setAccountType(Role accountType) {
        this.accountType = accountType;
    }

    public Language getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(Language preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }
}
