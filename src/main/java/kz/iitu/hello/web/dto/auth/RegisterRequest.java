package kz.iitu.hello.web.dto.auth;

import kz.iitu.hello.domain.enums.UserRole;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private UserRole role;
}
