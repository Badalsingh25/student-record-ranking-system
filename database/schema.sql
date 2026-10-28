-- MySQL 8+ schema for the Student Record & Ranking System.
-- Optional: the Spring Boot app creates/updates these tables automatically (ddl-auto=update).
-- Run this file only if you prefer to create the database yourself:
--   mysql -u root -p < database/schema.sql

CREATE DATABASE IF NOT EXISTS student_ranking_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_ranking_db;

CREATE TABLE IF NOT EXISTS students (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  roll_number   VARCHAR(30)  NOT NULL,
  full_name     VARCHAR(120) NOT NULL,
  email         VARCHAR(160) NOT NULL,
  phone         VARCHAR(20),
  class_name    VARCHAR(40)  NOT NULL,
  department    VARCHAR(80)  NOT NULL,
  year_of_study INT          NOT NULL,
  status        VARCHAR(20)  NOT NULL,
  created_at    DATETIME(6)  NOT NULL,
  updated_at    DATETIME(6)  NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_student_roll  (roll_number),
  UNIQUE KEY uk_student_email (email),
  KEY idx_student_class (class_name),
  KEY idx_student_name  (full_name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS academic_records (
  id             BIGINT        NOT NULL AUTO_INCREMENT,
  student_id     BIGINT        NOT NULL,
  term           VARCHAR(40)   NOT NULL,
  subject        VARCHAR(100)  NOT NULL,
  marks_obtained DECIMAL(7,2)  NOT NULL,
  max_marks      DECIMAL(7,2)  NOT NULL,
  PRIMARY KEY (id),
  KEY idx_record_term_subject (term, subject),
  CONSTRAINT fk_record_student FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE
) ENGINE=InnoDB;
