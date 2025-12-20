package com.example.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 클라이언트를 모바일로 만들시 있어야됨.
public class TokenRefreshRequest {
    //@NotBlank(message = "Refresh Token 필드는 필수입니다.")
    private String refreshToken;
}
