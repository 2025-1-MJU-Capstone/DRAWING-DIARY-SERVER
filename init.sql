CREATE DATABASE IF NOT EXISTS capstone
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE capstone;

CREATE TABLE member (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        login_id VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        social_provider VARCHAR(255) NOT NULL,
                        social_id VARCHAR(255),
                        created_at DATETIME NOT NULL,
                        PRIMARY KEY (id),
                        UNIQUE KEY uk_email_provider (email, social_provider)
);

CREATE TABLE font (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      font_name VARCHAR(255) NOT NULL,
                      font_file_name VARCHAR(255) NOT NULL UNIQUE,
                      ttf_url VARCHAR(255) NOT NULL,
                      member_id BIGINT NOT NULL,
                      created_at DATETIME NOT NULL,
                      PRIMARY KEY (id),
                      CONSTRAINT fk_font_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                      UNIQUE KEY uk_member_fontname (member_id, font_name)
);

CREATE TABLE diary (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       diary_date DATE NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       content TEXT NOT NULL,
                       image_file_name VARCHAR(255) NOT NULL,
                       image_url VARCHAR(255) NOT NULL,
                       member_id BIGINT NOT NULL,
                       font_id BIGINT DEFAULT NULL,
                       created_at DATETIME NOT NULL,
                       PRIMARY KEY (id),
                       CONSTRAINT fk_diary_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                       CONSTRAINT fk_diary_font FOREIGN KEY (font_id) REFERENCES font(id) ON DELETE SET NULL
);

CREATE TABLE refresh_token (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               token VARCHAR(255) NOT NULL,
                               member_id BIGINT NOT NULL UNIQUE,
                               expiry_date DATETIME NOT NULL,
                               created_at DATETIME NOT NULL,
                               PRIMARY KEY (id),
                               CONSTRAINT fk_refresh_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);