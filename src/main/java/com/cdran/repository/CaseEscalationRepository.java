package com.cdran.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdran.model.entity.CaseEscalation;

public interface CaseEscalationRepository extends JpaRepository<CaseEscalation, Long> {

    List<CaseEscalation> findByDiseaseCaseIdOrderByCreatedAtDesc(Long diseaseCaseId);

    boolean existsByDiseaseCaseIdAndResolvedFalse(Long diseaseCaseId);
}