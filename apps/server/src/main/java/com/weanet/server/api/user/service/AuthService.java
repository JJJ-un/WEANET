package com.weanet.server.api.user.service;

import com.weanet.server.api.user.domain.User;
import com.weanet.server.api.user.dto.request.UserLoginRequest;
import com.weanet.server.api.user.dto.response.UserLoginResponse;
import com.weanet.server.api.user.repository.UserRepository;
import com.weanet.server.global.error.exception.BusinessException;
import com.weanet.server.global.error.exception.ErrorCode;
import com.weanet.server.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인: 이메일과 비밀번호를 검증하고 JWT 토큰을 발급합니다.
     */
    public UserLoginResponse login(UserLoginRequest request) {
        // 1. 유저 존재 여부 확인
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 비밀번호 일치 여부 확인 (BCrypt 대조)
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. 인증 성공 시 토큰 생성
        String accessToken = jwtTokenProvider.createToken(user.getEmail());

        // 4. 응답 객체 생성 및 반환
        return new UserLoginResponse(accessToken, user.getNickname());
    }
}
