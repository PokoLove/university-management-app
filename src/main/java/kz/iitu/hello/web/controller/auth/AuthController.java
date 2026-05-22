package kz.iitu.hello.web.controller.auth;

import kz.iitu.hello.async.NotificationService;
import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.security.JwtUtil;
import kz.iitu.hello.web.dto.auth.AuthResponse;
import kz.iitu.hello.web.dto.auth.ChangePasswordRequest;
import kz.iitu.hello.web.dto.auth.LoginRequest;
import kz.iitu.hello.web.dto.auth.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, registration, and password management")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT token")
    public AuthResponse login(@RequestBody LoginRequest request) {
        log.info("Login attempt for username='{}'", request.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = usersRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        String token = jwtUtil.generateToken(request.getUsername());
        log.info("Login successful for username='{}', role={}", request.getUsername(), user.getRole());
        return new AuthResponse(token, user.getRole(), user.getId());
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user account (assigned GUEST role)")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        log.info("Registration attempt for username='{}'", request.getUsername());
        if (usersRepository.findByUserName(request.getUsername()).isPresent()) {
            log.warn("Registration failed — username='{}' already exists", request.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        User user = new User();
        user.setUserName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.GUEST);
        usersRepository.save(user);
        log.info("User registered: username='{}', id={}", user.getUserName(), user.getId());
        notificationService.notifyNewUserRegistered(user.getUserName(), user.getEmail());
        String token = jwtUtil.generateToken(user.getUserName());
        return new AuthResponse(token, user.getRole(), user.getId());
    }

    @PatchMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for the currently authenticated user")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Password change request for username='{}'", username);
        User user = usersRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Password change failed — wrong old password for username='{}'", username);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is invalid");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);
        log.info("Password changed successfully for username='{}'", username);
    }
}
