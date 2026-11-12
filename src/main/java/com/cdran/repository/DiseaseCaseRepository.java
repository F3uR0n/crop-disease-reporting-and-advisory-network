package com.cdran.repository;

import com.cdran.model.entity.DiseaseCase;
import com.cdran.model.enums.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Queries for disease cases. Farmers are identified by their login phone number,
 * so "farmer.phone" lets us filter by the logged-in user without loading the Farmer first.
 */
public interface DiseaseCaseRepository extends JpaRepository<DiseaseCase, Long> {

    List<DiseaseCase> findByFarmerPhoneOrderByCreatedAtDesc(String phone);

    List<DiseaseCase> findTop5ByFarmerPhoneOrderByCreatedAtDesc(String phone);

    /** Ownership check: returns empty if the case belongs to another farmer. */
    Optional<DiseaseCase> findByIdAndFarmerPhone(Long id, String phone);

    long countByFarmerPhoneAndStatus(String phone, CaseStatus status);

    List<DiseaseCase> findAllByOrderByCreatedAtDesc();
}
