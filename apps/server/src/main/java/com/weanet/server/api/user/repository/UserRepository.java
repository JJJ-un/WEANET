package com.weanet.server.api.user.repository;

import com.weanet.server.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일을 기준으로 유저를 찾습니다.
     * @param email 찾고자 하는 유저의 이메일
     * @return 유저 정보 (없을 수 있으므로 Optional로 감쌉니다)
     */
    Optional<User> findByEmail(String email);

    /**
     * 해당 이메일이 이미 존재하는지 확인합니다. (중복 가입 방지)
     */
    boolean existsByEmail(String email);

    /**
     * 해당 닉네임이 이미 존재하는지 확인합니다. (중복 닉네임 방지)
     */
    boolean existsByNickname(String nickname);
}
