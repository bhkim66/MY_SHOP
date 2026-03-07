package com.my_shop.member.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {

    ACTIVE("활성"),
    INACTIVE("휴면"),
    SUSPENDED("정지"),
    WITHDRAWN("탈퇴");

    private final String description;
}
