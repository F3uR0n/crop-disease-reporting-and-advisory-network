-- V1: user accounts. JPA inheritance strategy JOINED:
-- one shared "users" table + one table per role holding role-specific columns.
-- Never edit a migration that has already run; add V2__..., V3__... instead.

CREATE TABLE users (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    role                VARCHAR(20)  NOT NULL,
    full_name           VARCHAR(100) NOT NULL,
    phone               VARCHAR(15)  NOT NULL,
    email               VARCHAR(120),
    password_hash       VARCHAR(100) NOT NULL,
    preferred_language  VARCHAR(5)   NOT NULL,
    status              VARCHAR(20)  NOT NULL,
    created_at          TIMESTAMP(6) NOT NULL,
    updated_at          TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_phone UNIQUE (phone),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE farmers (
    id       BIGINT NOT NULL,
    village  VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT fk_farmers_user FOREIGN KEY (id) REFERENCES users (id)
);

CREATE TABLE experts (
    id                BIGINT       NOT NULL,
    qualification     VARCHAR(150) NOT NULL,
    institution       VARCHAR(150),
    max_active_cases  INT          NOT NULL,
    approved_at       TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_experts_user FOREIGN KEY (id) REFERENCES users (id)
);

CREATE TABLE field_officers (
    id           BIGINT      NOT NULL,
    employee_id  VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_field_officers_user FOREIGN KEY (id) REFERENCES users (id)
);

CREATE TABLE admins (
    id           BIGINT NOT NULL,
    designation  VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT fk_admins_user FOREIGN KEY (id) REFERENCES users (id)
);
