package com.example.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 카카오 OAuth 인가받을 주요 설정 항목들 저장
 * application.yaml의 oauth.kakao 설정 바인딩(binding)
 *
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth.kakao")
public class KakaoOAuthProperties {

    // 카카오 REST API KEY
    private String clientId;

    // 카카오 Client Secret Key
    private String clientSecret;

    // 카카오 인증 후 백엔드에 redirect 될 URL
    private String redirectUri;

    // 카카오 인가 코드 요청 URI
    private String authorizationUri;

    // 카카오 access 토큰 요청 URI
    private String tokenUri;

    // 카카오 사용자 정보 조회 URI
    private String userInfoUri;

}
