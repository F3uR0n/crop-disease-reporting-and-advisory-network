CREATE TABLE IF NOT EXISTS case_escalations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    disease_case_id BIGINT NOT NULL,
    escalated_by_id BIGINT NULL,
    predicted_label VARCHAR(160),
    confidence DOUBLE NOT NULL,
    threshold DOUBLE NOT NULL,
    reason VARCHAR(600) NOT NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_case_escalations_case FOREIGN KEY (disease_case_id) REFERENCES disease_cases(id),
    CONSTRAINT fk_case_escalations_actor FOREIGN KEY (escalated_by_id) REFERENCES users(id)
);

CREATE INDEX idx_case_escalations_case_resolved ON case_escalations (disease_case_id, resolved);