package com.my_shop.member.interfaces;

import com.my_shop.member.application.MemberService;
import com.my_shop.member.interfaces.dto.MemberRegisterRequest;
import com.my_shop.member.interfaces.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signup(@RequestBody MemberRegisterRequest request) {
        return ResponseEntity.ok(memberService.signup(request));
    }
}
