package com.cdran.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdran.model.entity.ForumPost;
import com.cdran.model.enums.PostStatus;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    List<ForumPost> findByStatusNotOrderByCreatedAtDesc(PostStatus hiddenStatus);

    @Query("""
            SELECT p FROM ForumPost p
            WHERE p.status <> :hiddenStatus
              AND (:cropId IS NULL OR (p.cropTag IS NOT NULL AND p.cropTag.id = :cropId))
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.body) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY p.createdAt DESC
            """)
    List<ForumPost> searchVisible(
            @Param("hiddenStatus") PostStatus hiddenStatus,
            @Param("cropId") Long cropId,
            @Param("keyword") String keyword);
}