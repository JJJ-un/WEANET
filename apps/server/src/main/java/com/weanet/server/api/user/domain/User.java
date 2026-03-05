package com.weanet.server.api.user.domain;

import com.weanet.server.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자 사이여야 합니다.")
    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(length = 100) // 소셜 로그인의 경우 null 가능
    private String password;

    @Column(unique = true) // 구글 고유 ID
    private String googleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider; // LOCAL, GOOGLE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // USER, ADMIN

    @Builder
    public User(String email, String nickname, String password, String googleId, AuthProvider authProvider, Role role) {
        // 🛡️ 필수값 검증 (비즈니스 로직)
        Assert.hasText(email, "이메일은 필수입니다.");
        Assert.hasText(nickname, "닉네임은 필수입니다.");
        
        // 🛡️ 일반 가입 시 비밀번호 필수 체크
        if (authProvider == null || authProvider == AuthProvider.LOCAL) {
            Assert.hasText(password, "일반 가입 시 비밀번호는 필수입니다.");
        }

        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.googleId = googleId;
        this.authProvider = authProvider != null ? authProvider : AuthProvider.LOCAL;
        this.role = role != null ? role : Role.USER;
    }

    public enum AuthProvider {
        LOCAL, GOOGLE
    }

    public enum Role {
        USER, ADMIN
    }
}
