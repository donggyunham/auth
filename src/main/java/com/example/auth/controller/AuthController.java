package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.exception.TokenException;
import com.example.auth.service.Authservice;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final Authservice authservice;

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("Authentication Service is running");
    }

    /**
     * {
     *     "email": "vorldmlqkfzjs@gmail.com",
     *     "password": "1234",
     *     "username": "함동균"
     * }
     * @param requestSignup
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody RequestSignup requestSignup) {

        ApiResponse<Void> response = authservice.signup(requestSignup);

        // 회원가입 성공 여부를 판단하여 response
        HttpStatusCode statusCode = response.getSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(statusCode).body(response);
    }

    @PostMapping("/old_login")
    public ResponseEntity<?> login(@Valid @RequestBody RequestLogin requestLogin){
        LoginResponse response = authservice.login(requestLogin);
        if (!(response.getAccessToken().isEmpty())){
            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponse.success("로그인 성공.", response)
            );
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("로그인 실패.")
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginEx(
            @Valid @RequestBody RequestLogin requestLogin,
            HttpServletRequest request,
            HttpServletResponse httpResponse
    ){
        LoginResponse response = authservice.loginEx(requestLogin);
        if (!(response.getAccessToken().isEmpty())){
            // 쿠키생성
            Cookie refreshTokenCookie = new Cookie("refreshToken", response.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);   // XSS 공격에 대응, JS에서 읽을 수 없음
            refreshTokenCookie.setSecure(false);    // 개발기간만 false, https를 적용하면 true
            refreshTokenCookie.setPath("/");        // 모든 경로에 쿠키 전송
            httpResponse.addCookie( refreshTokenCookie );

            // LoginResponse 인스턴스에 있는 refreshToken정보를 삭제.
            response.setRefreshToken(null);

            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponse.success("로그인 성공.", response)
            );
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("로그인 실패.")
            );
        }
    }

    public String extractRefreshTokenFromBody(TokenRefreshRequest body){
        if (body == null || body.getRefreshToken() == null || body.getRefreshToken().isBlank())
            return null;
        return body.getRefreshToken();
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();    // 쿠키는 복수개 존재할 수 있음, 따라서 배열로 받음.
        if (cookies == null) {
            // 에러를 발생시킴
            return null;
        }
        for (Cookie cookie : cookies) {
            // 쿠키 이름이 refreshToken인가
            if("refreshToken".equals(cookie.getName())){
                // 맞다면
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * AccessToken이 만료되면 RefreshToken을 이용하여 AccessToken을 재발급하는 요청 처리.
     * @param request
     * @return
     */
    @PostMapping("/refresh")
    @SuppressWarnings("NullableProblems")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(
            HttpServletRequest request                                     // web용
            //@RequestBody(required = false) @Valid TokenRefreshRequest body  //mobile
    ) {
        //String refreshToken = extractRefreshTokenFromBody( body );
        String refreshToken = extractRefreshTokenFromCookie( request );
        if ( refreshToken == null ) {
            //exception 발생시킴
            throw new TokenException("리프레시 토큰을 읽을 수 없거나, 유효하지 않습니다.");
        }

        // AccessToken을 재발급한 Response 객체를 반환 받음.
        TokenRefreshResponse tokenRefreshResponse = authservice.refreshAccessToken(refreshToken);
        tokenRefreshResponse.setRefreshToken(null);

        return ResponseEntity.ok(ApiResponse.success("Access Token 재발급 성공", tokenRefreshResponse));
    }

    @GetMapping("/logout")
    public void logout() {

    }
}
