package com.my_shop.member.interfaces.controller;

import com.my_shop.common.security.TokenDto;
import com.my_shop.member.application.MemberService;
import com.my_shop.member.interfaces.dto.LoginRequest;
import com.my_shop.member.interfaces.dto.MemberResponse;
import com.my_shop.member.interfaces.dto.TokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(memberService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(memberService.getMember(Long.parseLong(userDetails.getUsername())));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequest request) {
        return ResponseEntity.ok(memberService.reissue(request));
    }
}
