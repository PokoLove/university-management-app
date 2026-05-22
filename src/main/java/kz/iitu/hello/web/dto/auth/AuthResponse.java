package kz.iitu.hello.web.dto.auth;

import kz.iitu.hello.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserRole role;
    private Long userId;
}
