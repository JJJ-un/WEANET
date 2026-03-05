package com.weanet.server.api.user.controller;

import com.weanet.server.api.user.dto.request.UserSignupRequest;
import com.weanet.server.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "회원가입 및 로그인 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임을 사용하여 새로운 유저를 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@Valid @RequestBody UserSignupRequest request) {
        Long userId = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }
}
