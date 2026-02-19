CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE roles (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE api_keys (
    id          BIGSERIAL PRIMARY KEY,
    key_hash    VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    expires_at  TIMESTAMP,
    user_id     BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE api_key_roles (
    api_key_id BIGINT NOT NULL REFERENCES api_keys (id) ON DELETE CASCADE,
    role_id    BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (api_key_id, role_id)
);

-- Roles padrão
INSERT INTO roles (name) VALUES
    ('ADMIN'),
    ('FISCAL_READ'),
    ('FISCAL_WRITE'),
    ('FINANCEIRO_READ'),
    ('FINANCEIRO_WRITE');

-- usuário admin (senha: admin)
INSERT INTO users (username, email, password, active, created_at)
VALUES ('admin', 'julio@prestobr.com', '$2a$12$cRM0Sb4HK.TA9GwQAgUewuOsa.dhgExpbdYJdpzMzrzSXuU6cYA5m', TRUE, NOW());

-- vincula admin à role ADMIN
INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM users WHERE username = 'admin'),
           (SELECT id FROM roles WHERE name = 'ADMIN')
       );