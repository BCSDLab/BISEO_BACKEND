CREATE TABLE IF NOT EXISTS Notice (
    id          BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT          NOT NULL,
    title       VARCHAR(255)    NOT NULL,
    content     TEXT            NOT NULL,
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted  TINYINT(1)      NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS Notice_Target (
    id          BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    notice_id   BIGINT      NOT NULL,
    target      INT         NOT NULL,
    created_at  DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at  DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted  TINYINT(1)      NOT NULL DEFAULT 0,
    FOREIGN KEY (notice_id) REFERENCES Notice(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Notice_Read (
    id          BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    notice_id   BIGINT      NOT NULL,
    user_id     BIGINT      NOT NULL,
    read_at     DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    is_deleted  TINYINT(1)      NOT NULL DEFAULT 0,
    FOREIGN KEY (notice_id) REFERENCES Notice(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Notice_File (
    id          BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    notice_id   BIGINT      NOT NULL,
    path        TEXT        NOT NULL,
    saved_name  TEXT        NOT NULL,
    type        VARCHAR(255) NOT NULL,
    created_at  DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at  DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted  TINYINT(1)      NOT NULL DEFAULT 0,
    FOREIGN KEY (notice_id) REFERENCES Notice(id) ON DELETE CASCADE
);
