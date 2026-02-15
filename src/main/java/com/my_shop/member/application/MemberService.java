package com.my_shop.member.application;

import com.my_shop.common.security.JwtTokenProvider;
import com.my_shop.common.security.TokenDto;
import com.my_shop.common.security.CustomUserDetailsService;
import com.my_shop.market.domain.entity.Market;
import com.my_shop.market.infrastructure.MarketRepository;
import com.my_shop.member.domain.entity.MemberRole;
import com.my_shop.member.domain.entity.RefreshToken;
import com.my_shop.member.domain.entity.User;
import com.my_shop.member.infrastructure.RefreshTokenRepository;
import com.my_shop.member.infrastructure.UserRepository;
import com.my_shop.member.interfaces.dto.LoginRequest;
import com.my_shop.member.interfaces.dto.MemberRegisterRequest;
import com.my_shop.member.interfaces.dto.MemberResponse;
import com.my_shop.member.interfaces.dto.TokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserRepository userRepository;
    private final MarketRepository marketRepository; // Market 생성용 (아직 없을 수 있음, 확인 필요)
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public MemberResponse signup(MemberRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }

        User user = User.create(
                request.getEmail(), // Login ID를 Email로 사용
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getRoleEnum());

        User savedUser = userRepository.save(user);

        // SELLER인 경우 Market 자동 생성
        if (request.getRoleEnum() == MemberRole.SELLER) {
            String marketSlug = request.getEmail().replace("@", "-").replace(".", "-");
            String marketName = request.getName() + "'s Shop";

            Market market = Market.create(
                    savedUser,
                    marketName,
                    marketSlug,
                    null, // description은 선택사항
                    "ACTIVE");
            marketRepository.save(market);
        }

        return MemberResponse.of(savedUser);
    }

    @Transactional
    public TokenDto login(LoginRequest request) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        // authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername
        // 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);

        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        // 5. 토큰 발급
        return tokenDto;
    }

    @Transactional
    public TokenDto reissue(TokenRequest request) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(request.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(request.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        return tokenDto;
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long seq) {
        return userRepository.findById(seq)
                .map(MemberResponse::of)
                .orElseThrow(() -> new RuntimeException("유저 정보가 없습니다."));
    }
}
