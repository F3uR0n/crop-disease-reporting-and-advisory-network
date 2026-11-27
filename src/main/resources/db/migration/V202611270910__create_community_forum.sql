CREATE TABLE IF NOT EXISTS forum_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    author_id BIGINT NOT NULL,
    crop_id BIGINT NULL,
    title VARCHAR(200) NOT NULL,
    body VARCHAR(3000) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_forum_posts_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_forum_posts_crop FOREIGN KEY (crop_id) REFERENCES crops(id)
);

CREATE TABLE IF NOT EXISTS forum_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    body VARCHAR(2500) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_by_id BIGINT NULL,
    verified_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_forum_answers_post FOREIGN KEY (post_id) REFERENCES forum_posts(id),
    CONSTRAINT fk_forum_answers_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_forum_answers_verifier FOREIGN KEY (verified_by_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS content_flags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reporter_id BIGINT NOT NULL,
    resolved_by_id BIGINT NULL,
    target_type VARCHAR(32) NOT NULL,
    target_id BIGINT NOT NULL,
    reason VARCHAR(600) NOT NULL,
    status VARCHAR(32) NOT NULL,
    action_taken VARCHAR(300),
    resolved_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_content_flags_reporter FOREIGN KEY (reporter_id) REFERENCES users(id),
    CONSTRAINT fk_content_flags_resolver FOREIGN KEY (resolved_by_id) REFERENCES users(id)
);

CREATE INDEX idx_forum_posts_status_created ON forum_posts (status, created_at);
CREATE INDEX idx_forum_answers_post_created ON forum_answers (post_id, created_at);
CREATE INDEX idx_content_flags_status_target ON content_flags (status, target_type, target_id);