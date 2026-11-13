package com.cdran.service;

import com.cdran.model.entity.User;
import com.cdran.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Bridge between our User entity and Spring Security. Called on every login attempt.
 * Username = phone number. Non-ACTIVE accounts (pending experts, suspended users) are disabled.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userRepository.findByPhone(phone.trim())
                .orElseThrow(() -> new UsernameNotFoundException("No user with phone " + phone));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getPhone())
                .password(user.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .disabled(!user.isActive())
                .build();
    }
}
