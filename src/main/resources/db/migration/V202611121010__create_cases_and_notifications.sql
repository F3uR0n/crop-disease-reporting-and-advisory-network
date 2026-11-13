-- Feature 1 (case submission) and Feature 4 (case tracking + notifications).
-- Images are stored as files on disk; case_images only keeps the relative file path.

CREATE TABLE disease_cases (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    farmer_id     BIGINT        NOT NULL,
    crop_id       BIGINT        NOT NULL,
    growth_stage  VARCHAR(20)   NOT NULL,
    description   VARCHAR(1000),
    latitude      DOUBLE        NOT NULL,
    longitude     DOUBLE        NOT NULL,
    status        VARCHAR(20)   NOT NULL,
    created_at    TIMESTAMP(6)  NOT NULL,
    updated_at    TIMESTAMP(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_cases_farmer FOREIGN KEY (farmer_id) REFERENCES farmers (id),
    CONSTRAINT fk_cases_crop   FOREIGN KEY (crop_id)   REFERENCES crops (id)
);

CREATE TABLE case_symptoms (
    case_id     BIGINT NOT NULL,
    symptom_id  BIGINT NOT NULL,
    PRIMARY KEY (case_id, symptom_id),
    CONSTRAINT fk_case_symptoms_case    FOREIGN KEY (case_id)    REFERENCES disease_cases (id),
    CONSTRAINT fk_case_symptoms_symptom FOREIGN KEY (symptom_id) REFERENCES symptoms (id)
);

CREATE TABLE case_images (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    case_id          BIGINT        NOT NULL,
    file_path        VARCHAR(255)  NOT NULL,
    width            INT           NOT NULL,
    height           INT           NOT NULL,
    sharpness_score  DOUBLE        NOT NULL,
    created_at       TIMESTAMP(6)  NOT NULL,
    updated_at       TIMESTAMP(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_case_images_case FOREIGN KEY (case_id) REFERENCES disease_cases (id)
);

CREATE TABLE case_status_history (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    case_id      BIGINT        NOT NULL,
    from_status  VARCHAR(20),
    to_status    VARCHAR(20)   NOT NULL,
    note         VARCHAR(255),
    created_at   TIMESTAMP(6)  NOT NULL,
    updated_at   TIMESTAMP(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_case_history_case FOREIGN KEY (case_id) REFERENCES disease_cases (id)
);

CREATE TABLE notifications (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    user_id      BIGINT        NOT NULL,
    type         VARCHAR(20)   NOT NULL,
    message_key  VARCHAR(100)  NOT NULL,
    message_arg  VARCHAR(100),
    link         VARCHAR(255),
    read_at      TIMESTAMP(6),
    created_at   TIMESTAMP(6)  NOT NULL,
    updated_at   TIMESTAMP(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_cases_farmer ON disease_cases (farmer_id);
CREATE INDEX idx_notifications_user ON notifications (user_id);
