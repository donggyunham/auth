package com.example.auth.controller;

import com.example.auth.dto.ApiResponse;
import com.example.auth.dto.LoginResponse;
import com.example.auth.dto.RequestLogin;
import com.example.auth.dto.kakao.KakaoTokenResponse;
import com.example.auth.dto.kakao.KakaoUserResponse;
import com.example.auth.service.KakaoAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth/kakao")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    /**
     * 카카오 로그인 시작 라우트(Route)
     * 사용자를 카카오 로그인 페이지로 리다이렉트 시킴
     * @param response
     * @throws IOException
     */
    @GetMapping("/login")   // http://localhost:8070/oauth/kakao/login
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        String authorizationUrl = kakaoAuthService.getAuthorizationUrl();
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/callback")    // http://localhost:8070/oauth/kakao/callback
    public ResponseEntity<ApiResponse<LoginResponse>> kakaoCallback(
            @RequestParam String code,
            HttpServletResponse httpResponse){
        System.out.println("code from kakao : " + code);

        try {
            // 카카오 인가 서버에게 access token을 발급받으러 token uri 다시 호출
            KakaoTokenResponse kakaoTokenResponse = kakaoAuthService.getAccessToken(code);

            KakaoUserResponse kakaoUserResponse = kakaoAuthService.getUserInfo((kakaoTokenResponse.getAccessToken()));

            // 데이터베이스에서 조회하고 없으면 저장
            LoginResponse response = kakaoAuthService.loginByKakao(kakaoUserResponse);

            // RefreshToken cookie설정

                Cookie refreshTokenCookie = new Cookie("refreshToken", response.getRefreshToken());
                refreshTokenCookie.setHttpOnly(true);           // XSS 공격에 대응, JS
                refreshTokenCookie.setSecure(false);            // 개발기간만 false, https를 적용하면 true
                refreshTokenCookie.setPath("/");                // 모든 경로에 쿠키 전송
                httpResponse.addCookie(refreshTokenCookie);     // 헤더부분에 쿠키 설정

                // LoginResponse 인스턴스에 있는 refreshToken정보를 삭제.
                response.setRefreshToken(null);

                return ResponseEntity.status(HttpStatus.OK).body(
                        ApiResponse.success("카카오 로그인 성공", response)
                );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("카카오 로그인 실패: " + e.getMessage())
            );
        }
    }
}
