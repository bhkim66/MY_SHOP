package com.my_shop.member.interfaces.dto;

import com.my_shop.member.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private String email;
    private String name;

    public static MemberResponse of(User user) {
        return new MemberResponse(user.getEmail(), user.getName());
    }
}
