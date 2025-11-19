package com.motmap.repository;

import com.motmap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 사용자 조회
     *
     * @param username 사용자명
     * @return Optional<User>
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자명 존재 여부 확인
     *
     * @param username 사용자명
     * @return boolean
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 이메일
     * @return boolean
     */
    boolean existsByEmail(String email);
}

