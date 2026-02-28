package com.my_shop.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || authentication.getPrincipal().equals("anonymousUser")) {
                return Optional.of(0L); // 로그인하지 않은 경우 0L (System)
            }
            try {
                // Principal이 UserDetails(User)인 경우 seq를 파싱해서 리턴
                if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                    org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication
                            .getPrincipal();
                    return Optional.of(Long.parseLong(user.getUsername())); // CustomUserDetailsService에서 username에 seq를
                                                                            // 넣었음
                }
                return Optional.of(0L);
            } catch (Exception e) {
                return Optional.of(0L);
            }
        };
    }
}
