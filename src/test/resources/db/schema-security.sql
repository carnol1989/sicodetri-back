--src/test/resources/db/schema-security.sql
CREATE TABLE IF NOT EXISTS users (
  id                SERIAL PRIMARY KEY,
  username          VARCHAR(50)  NOT NULL UNIQUE,
  password          VARCHAR(100) NOT NULL,
  enabled           BOOLEAN      NOT NULL DEFAULT TRUE,
  added_by          VARCHAR(50),
  addition_date     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified_by       VARCHAR(50),
  modification_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
  id                SERIAL PRIMARY KEY,
  name              VARCHAR(50)  NOT NULL UNIQUE,
  enabled           BOOLEAN      NOT NULL DEFAULT TRUE,
  added_by          VARCHAR(50),
  addition_date     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified_by       VARCHAR(50),
  modification_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id           INT          NOT NULL,
  role_id           INT          NOT NULL,
  enabled           BOOLEAN      NOT NULL DEFAULT TRUE,
  added_by          VARCHAR(50),
  addition_date     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified_by       VARCHAR(50),
  modification_date TIMESTAMP,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);