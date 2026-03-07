package com.my_shop.member.interfaces.dto;

import com.my_shop.member.domain.entity.MemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberRegisterRequest {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}", message = "비밀번호는 영문 대,소문자, 숫자, 특수기호를 포함하여 8~20자 이내로 입력해주세요.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "휴대폰 번호는 필수 입력 값입니다.")
    private String phone;

    @NotBlank(message = "권한은 필수 입력 값입니다.")
    private String role; // BUYER, SELLER (String으로 받고 내부에서 Enum 변환)

    // Seller Only
    private String shopName;
    private String businessNumber;

    public MemberRole getRoleEnum() {
        try {
            return MemberRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return MemberRole.BUYER; // Default
        }
    }
}
