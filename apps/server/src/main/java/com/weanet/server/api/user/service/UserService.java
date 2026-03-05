package com.weanet.server.api.user.service;

import com.weanet.server.api.user.domain.User;
import com.weanet.server.api.user.dto.request.UserSignupRequest;
import com.weanet.server.api.user.repository.UserRepository;
import com.weanet.server.global.error.exception.BusinessException;
import com.weanet.server.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입: 중복 체크 후 비밀번호를 암호화하여 저장합니다.
     */
    @Transactional
    public Long signup(UserSignupRequest request) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. 닉네임 중복 체크
        if (userRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        // 3. 비밀번호 암호화 및 유저 생성
        User user = User.builder()
                .email(request.email())
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .authProvider(User.AuthProvider.LOCAL)
                .role(User.Role.USER)
                .build();

        // 4. DB 저장 후 ID 반환
        return userRepository.save(user).getId();
    }
}
