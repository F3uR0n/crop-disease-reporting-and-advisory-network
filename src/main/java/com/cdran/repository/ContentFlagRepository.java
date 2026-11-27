package com.cdran.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdran.model.entity.ContentFlag;
import com.cdran.model.enums.FlagStatus;

public interface ContentFlagRepository extends JpaRepository<ContentFlag, Long> {

    List<ContentFlag> findByStatusOrderByCreatedAtDesc(FlagStatus status);
}