package com.cdran.service;

import com.cdran.dto.RegistrationForm;
import com.cdran.exception.BusinessRuleException;
import com.cdran.model.entity.Expert;
import com.cdran.model.entity.Farmer;
import com.cdran.model.entity.User;
import com.cdran.model.enums.Role;
import com.cdran.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Business rules for self-registration. The controller only passes the validated form in;
 * every rule about who may register and how lives here.
 */
@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegistrationForm form) {
        if (!form.passwordsMatch()) {
            throw new BusinessRuleException("confirmPassword", "validation.password.mismatch");
        }
        String phone = form.getPhone().trim();
        if (userRepository.existsByPhone(phone)) {
            throw new BusinessRuleException("phone", "validation.phone.taken");
        }
        String email = StringUtils.hasText(form.getEmail()) ? form.getEmail().trim().toLowerCase() : null;
        if (email != null && userRepository.existsByEmail(email)) {
            throw new BusinessRuleException("email", "validation.email.taken");
        }

        String hash = passwordEncoder.encode(form.getPassword());
        String name = form.getFullName().trim();

        User user;
        if (form.getAccountType() == Role.FARMER) {
            user = new Farmer(name, phone, email, hash, form.getPreferredLanguage());
        } else if (form.getAccountType() == Role.EXPERT) {
            if (!StringUtils.hasText(form.getQualification())) {
                throw new BusinessRuleException("qualification", "validation.required");
            }
            // Expert constructor sets status PENDING_APPROVAL: no login until an admin approves.
            user = new Expert(name, phone, email, hash, form.getPreferredLanguage(),
                    form.getQualification().trim(), form.getInstitution());
        } else {
            throw new BusinessRuleException("accountType", "validation.accountType");
        }
        return userRepository.save(user);
    }
}
