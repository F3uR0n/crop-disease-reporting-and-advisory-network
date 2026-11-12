package com.cdran.config;

import com.cdran.model.entity.Admin;
import com.cdran.model.enums.Language;
import com.cdran.repository.AdminRepository;
import com.cdran.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Creates the first admin on startup if there is none, using app.admin.phone / app.admin.password
 * (set via environment variables or .env). Admins cannot self-register, so this bootstraps the system.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.phone:}")
    private String adminPhone;

    @Value("${app.admin.password:}")
    private String adminPassword;

    public DataInitializer(AdminRepository adminRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminRepository.count() > 0) {
            return;
        }
        if (!StringUtils.hasText(adminPhone) || !StringUtils.hasText(adminPassword)) {
            log.warn("No admin account exists. Set ADMIN_PHONE and ADMIN_PASSWORD to create one.");
            return;
        }
        if (userRepository.existsByPhone(adminPhone)) {
            log.warn("ADMIN_PHONE {} already belongs to a non-admin user; admin not created.", adminPhone);
            return;
        }
        adminRepository.save(new Admin("System Admin", adminPhone, null,
                passwordEncoder.encode(adminPassword), Language.EN));
        log.info("Created initial admin account for phone {}", adminPhone);
    }
}
