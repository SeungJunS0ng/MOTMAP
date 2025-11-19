-- src/main/resources/db/migration/V1__init.sql
-- Flyway 초기 마이그레이션: restaurants 테이블 생성

CREATE TABLE IF NOT EXISTS restaurants (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(50),
  address VARCHAR(500),
  latitude DOUBLE,
  longitude DOUBLE,
  rating DECIMAL(2,1) DEFAULT 0.0,
  review TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uq_restaurant_name_address (name, address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

