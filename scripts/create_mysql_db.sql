-- scripts/create_mysql_db.sql
-- MySQL에서 motmap 데이터베이스와 'admin' 사용자(로컬 접속 허용)를 생성하고 권한을 부여합니다.
-- 이 스크립트는 MySQL 루트 또는 권한 있는 계정으로 실행해야 합니다.

CREATE DATABASE IF NOT EXISTS motmap CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- localhost, 127.0.0.1, 그리고 모든 호스트(%)에 대해 사용자 생성
CREATE USER IF NOT EXISTS 'admin'@'localhost' IDENTIFIED BY '091122john';
CREATE USER IF NOT EXISTS 'admin'@'127.0.0.1' IDENTIFIED BY '091122john';
CREATE USER IF NOT EXISTS 'admin'@'%' IDENTIFIED BY '091122john';

-- 권한 부여
GRANT ALL PRIVILEGES ON motmap.* TO 'admin'@'localhost';
GRANT ALL PRIVILEGES ON motmap.* TO 'admin'@'127.0.0.1';
GRANT ALL PRIVILEGES ON motmap.* TO 'admin'@'%';
FLUSH PRIVILEGES;

-- 확인용: 현재 사용자 목록 보기 (옵션)
-- SELECT User, Host FROM mysql.user WHERE User = 'admin';

