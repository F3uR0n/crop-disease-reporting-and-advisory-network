package com.cdran.repository;

import com.cdran.model.entity.User;
import com.cdran.model.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Data access for all user types. Spring Data generates the SQL from the method names.
 * Repositories hold no business rules - those belong in services.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    List<User> findByStatus(AccountStatus status);
}
