package com.cdran.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdran.model.entity.DiagnosisFeedback;

public interface DiagnosisFeedbackRepository extends JpaRepository<DiagnosisFeedback, Long> {

    Optional<DiagnosisFeedback> findByDiseaseCaseId(Long diseaseCaseId);

    long countByAiWasCorrectTrue();

    List<DiagnosisFeedback> findAllByOrderByUpdatedAtDesc();
}