package com.my_shop.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my_shop.common.security.TokenDto;
import com.my_shop.member.interfaces.dto.LoginRequest;
import com.my_shop.member.interfaces.dto.MemberRegisterRequest;
import com.my_shop.member.interfaces.dto.TokenRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test") // application-test.properties 가 있다면 사용
class MemberIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("회원가입 성공 테스트")
        void signup_success() throws Exception {
                // given
                MemberRegisterRequest request = createRegisterRequest("test@test.com", "Password123!", "테스터",
                                "010-1234-5678",
                                "BUYER");

                // when & then
                mockMvc.perform(post("/v1/members/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("test@test.com"))
                                .andExpect(jsonPath("$.name").value("테스터"));
        }

        @Test
        @DisplayName("로그인 및 토큰 발급 테스트")
        void login_success() throws Exception {
                // given
                MemberRegisterRequest signupRequest = createRegisterRequest("login@test.com", "Password123!", "로그인테스터",
                                "010-1111-2222", "BUYER");
                mockMvc.perform(post("/v1/members/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signupRequest)))
                                .andDo(print())
                                .andExpect(status().isOk());

                LoginRequest loginRequest = createLoginRequest("login@test.com", "Password123!");

                // when & then
                mockMvc.perform(post("/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").exists())
                                .andExpect(jsonPath("$.refreshToken").exists());
        }

        @Test
        @DisplayName("토큰 재발급 테스트")
        void reissue_success() throws Exception {
                // given
                // 1. 회원가입
                MemberRegisterRequest signupRequest = createRegisterRequest("reissue@test.com", "Password123!",
                                "재발급테스터",
                                "010-3333-4444", "BUYER");
                mockMvc.perform(post("/v1/members/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signupRequest)))
                                .andDo(print())
                                .andExpect(status().isOk());

                // 2. 로그인
                LoginRequest loginRequest = createLoginRequest("reissue@test.com", "Password123!");
                MvcResult loginResult = mockMvc.perform(post("/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andReturn();

                String responseBody = loginResult.getResponse().getContentAsString();
                TokenDto tokenDto = objectMapper.readValue(responseBody, TokenDto.class);

                // 3. 재발급 요청
                TokenRequest tokenRequest = createTokenRequest(tokenDto.getAccessToken(), tokenDto.getRefreshToken());

                // when & then
                mockMvc.perform(post("/v1/auth/reissue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tokenRequest)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").exists())
                                .andExpect(jsonPath("$.refreshToken").exists());
        }

        @Test
        @DisplayName("내 정보 조회 테스트")
        void getMe_success() throws Exception {
                // given
                // 1. 회원가입
                MemberRegisterRequest signupRequest = createRegisterRequest("me@test.com", "Password123!", "마이페이지",
                                "010-5555-6666", "BUYER");
                mockMvc.perform(post("/v1/members/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signupRequest)));

                // 2. 로그인
                LoginRequest loginRequest = createLoginRequest("me@test.com", "Password123!");
                MvcResult loginResult = mockMvc.perform(post("/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andReturn();

                String responseBody = loginResult.getResponse().getContentAsString();
                TokenDto tokenDto = objectMapper.readValue(responseBody, TokenDto.class);

                // when & then
                mockMvc.perform(get("/v1/auth/me")
                                .header("Authorization", "Bearer " + tokenDto.getAccessToken()))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("me@test.com"))
                                .andExpect(jsonPath("$.name").value("마이페이지"))
                                .andExpect(jsonPath("$.role").value("BUYER"))
                                .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        private MemberRegisterRequest createRegisterRequest(String email, String password, String name, String phone,
                        String role) throws Exception {
                MemberRegisterRequest request = new MemberRegisterRequest();
                setField(request, "email", email);
                setField(request, "password", password);
                setField(request, "name", name);
                setField(request, "phone", phone);
                setField(request, "role", role);
                return request;
        }

        private LoginRequest createLoginRequest(String email, String password) throws Exception {
                LoginRequest request = new LoginRequest();
                setField(request, "email", email);
                setField(request, "password", password);
                return request;
        }

        private TokenRequest createTokenRequest(String accessToken, String refreshToken) throws Exception {
                TokenRequest request = new TokenRequest();
                setField(request, "accessToken", accessToken);
                setField(request, "refreshToken", refreshToken);
                return request;
        }

        private void setField(Object object, String fieldName, Object value) throws Exception {
                Field field = object.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, value);
        }
}
