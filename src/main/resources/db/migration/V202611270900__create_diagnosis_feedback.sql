CREATE TABLE IF NOT EXISTS diagnosis_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    disease_case_id BIGINT NOT NULL UNIQUE,
    submitted_by_id BIGINT NOT NULL,
    decision VARCHAR(32) NOT NULL,
    recovery_outcome VARCHAR(32) NOT NULL,
    follow_up_status VARCHAR(32) NOT NULL,
    ai_label VARCHAR(160),
    expert_label VARCHAR(160),
    ai_confidence DOUBLE NOT NULL DEFAULT 0.0,
    ai_was_correct BOOLEAN NOT NULL DEFAULT FALSE,
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_diag_feedback_case FOREIGN KEY (disease_case_id) REFERENCES disease_cases(id),
    CONSTRAINT fk_diag_feedback_user FOREIGN KEY (submitted_by_id) REFERENCES users(id)
);

CREATE INDEX idx_diag_feedback_correct ON diagnosis_feedback (ai_was_correct);