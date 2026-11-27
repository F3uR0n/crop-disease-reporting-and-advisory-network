package com.cdran.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdran.model.entity.ForumAnswer;

public interface ForumAnswerRepository extends JpaRepository<ForumAnswer, Long> {

    List<ForumAnswer> findByPostIdOrderByCreatedAtAsc(Long postId);
}