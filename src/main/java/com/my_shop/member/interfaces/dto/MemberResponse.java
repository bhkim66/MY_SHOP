package com.my_shop.member.interfaces.dto;

import com.my_shop.member.domain.entity.MemberRole;
import com.my_shop.member.domain.entity.MemberStatus;
import com.my_shop.member.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private Long seq;
    private String loginId;
    private String email;
    private String name;
    private MemberRole role;
    private MemberStatus status;

    public static MemberResponse of(User user) {
        return new MemberResponse(
                user.getSeq(),
                user.getLoginId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getStatus());
    }
}
