package com.my_shop.member.domain.entity;

import com.my_shop.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_login_id", columnList = "login_id", unique = true),
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_status_role", columnList = "status, role")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @Column(name = "login_id", nullable = false, length = 50, unique = true)
    private String loginId;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private MemberRole role; // USER, SELLER, ADMIN

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MemberStatus status; // ACTIVE, INACTIVE, SUSPENDED, WITHDRAWN

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "phone_verified", nullable = false)
    private boolean phoneVerified;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public static User create(String loginId, String password, String name, String email, String phone,
            MemberRole role) {
        User user = new User();
        user.loginId = loginId;
        user.password = password;
        user.name = name;
        user.email = email;
        user.phone = phone;
        user.role = role;
        user.status = MemberStatus.ACTIVE;
        user.emailVerified = false;
        user.phoneVerified = false;
        return user;
    }
}
