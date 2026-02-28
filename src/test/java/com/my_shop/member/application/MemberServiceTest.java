package com.my_shop.member.application;

import com.my_shop.common.security.JwtTokenProvider;
import com.my_shop.common.security.TokenDto;
import com.my_shop.member.domain.entity.MemberRole;
import com.my_shop.member.domain.entity.RefreshToken;
import com.my_shop.member.domain.entity.User;
import com.my_shop.member.infrastructure.RefreshTokenRepository;
import com.my_shop.member.infrastructure.UserRepository;
import com.my_shop.member.interfaces.dto.LoginRequest;
import com.my_shop.member.interfaces.dto.MemberRegisterRequest;
import com.my_shop.member.interfaces.dto.MemberResponse;
import com.my_shop.member.interfaces.dto.TokenRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Nested
    @DisplayName("회원가입")
    class Signup {

        @Test
        @DisplayName("성공: 새로운 회원을 저장한다.")
        void signup_success() {
            // given
            MemberRegisterRequest request = new MemberRegisterRequest();
            ReflectionTestUtils.setField(request, "email", "test@test.com");
            ReflectionTestUtils.setField(request, "password", "password123!");
            ReflectionTestUtils.setField(request, "name", "홍길동");
            ReflectionTestUtils.setField(request, "phone", "01012345678");
            ReflectionTestUtils.setField(request, "role", "BUYER");

            given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
            given(passwordEncoder.encode(any())).willReturn("encodedPassword");

            User user = User.create(request.getEmail(), "encodedPassword", request.getName(), request.getEmail(),
                    request.getPhone(), MemberRole.BUYER);
            given(userRepository.save(any(User.class))).willReturn(user);

            // when
            MemberResponse response = memberService.signup(request);

            // then
            assertThat(response.getEmail()).isEqualTo(request.getEmail());
            assertThat(response.getName()).isEqualTo(request.getName());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("실패: 이미 존재하는 이메일인 경우 예외가 발생한다.")
        void signup_fail_duplicate_email() {
            // given
            MemberRegisterRequest request = new MemberRegisterRequest();
            ReflectionTestUtils.setField(request, "email", "duplicate@test.com");

            given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> memberService.signup(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("이미 가입되어 있는 유저입니다");
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("성공: 토큰을 발급하고 RefreshToken을 저장한다.")
        void login_success() {
            // given
            LoginRequest request = new LoginRequest();
            ReflectionTestUtils.setField(request, "email", "test@test.com");
            ReflectionTestUtils.setField(request, "password", "password123!");

            Authentication authentication = mock(Authentication.class);
            org.springframework.security.authentication.AuthenticationManager authenticationManager = mock(
                    org.springframework.security.authentication.AuthenticationManager.class);

            given(authenticationManagerBuilder.getObject()).willReturn(authenticationManager);
            given(authenticationManager.authenticate(any())).willReturn(authentication);

            TokenDto tokenDto = TokenDto.builder()
                    .grantType("Bearer")
                    .accessToken("access-token")
                    .refreshToken("refresh-token")
                    .build();
            given(jwtTokenProvider.generateTokenDto(authentication)).willReturn(tokenDto);
            given(authentication.getName()).willReturn("test@test.com");

            // when
            TokenDto result = memberService.login(request);

            // then
            assertThat(result.getAccessToken()).isEqualTo("access-token");
            assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
            verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        }
    }

    @Nested
    @DisplayName("토큰 재발급")
    class Reissue {

        @Test
        @DisplayName("성공: 새로운 토큰을 발급한다.")
        void reissue_success() {
            // given
            TokenRequest request = new TokenRequest();
            ReflectionTestUtils.setField(request, "accessToken", "old-access");
            ReflectionTestUtils.setField(request, "refreshToken", "old-refresh");

            given(jwtTokenProvider.validateToken(request.getRefreshToken())).willReturn(true);

            Authentication authentication = mock(Authentication.class);
            given(jwtTokenProvider.getAuthentication(request.getAccessToken())).willReturn(authentication);
            given(authentication.getName()).willReturn("user123");

            RefreshToken refreshToken = RefreshToken.builder()
                    .key("user123")
                    .value("old-refresh")
                    .build();
            given(refreshTokenRepository.findByKey("user123")).willReturn(Optional.of(refreshToken));

            TokenDto newTokenDto = TokenDto.builder()
                    .accessToken("new-access")
                    .refreshToken("new-refresh")
                    .build();
            given(jwtTokenProvider.generateTokenDto(authentication)).willReturn(newTokenDto);

            // when
            TokenDto result = memberService.reissue(request);

            // then
            assertThat(result.getAccessToken()).isEqualTo("new-access");
            verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("실패: Refresh Token이 유효하지 않으면 예외가 발생한다.")
        void reissue_fail_invalid_token() {
            // given
            TokenRequest request = new TokenRequest();
            ReflectionTestUtils.setField(request, "refreshToken", "invalid-token");

            given(jwtTokenProvider.validateToken(request.getRefreshToken())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> memberService.reissue(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Refresh Token 이 유효하지 않습니다.");
        }

        @Test
        @DisplayName("실패: 토큰 정보가 일치하지 않으면 예외가 발생한다.")
        void reissue_fail_mismatch_token() {
            // given
            TokenRequest request = new TokenRequest();
            ReflectionTestUtils.setField(request, "accessToken", "access");
            ReflectionTestUtils.setField(request, "refreshToken", "wrong-refresh");

            given(jwtTokenProvider.validateToken(any())).willReturn(true);

            Authentication authentication = mock(Authentication.class);
            given(jwtTokenProvider.getAuthentication(any())).willReturn(authentication);
            given(authentication.getName()).willReturn("user123");

            RefreshToken refreshToken = RefreshToken.builder()
                    .key("user123")
                    .value("correct-refresh")
                    .build();
            given(refreshTokenRepository.findByKey("user123")).willReturn(Optional.of(refreshToken));

            // when & then
            assertThatThrownBy(() -> memberService.reissue(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("토큰의 유저 정보가 일치하지 않습니다.");
        }
    }
}
