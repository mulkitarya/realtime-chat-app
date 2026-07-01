package com.mulkit.chat.app.controller;

import com.mulkit.chat.app.dto.AuthRequest;
import com.mulkit.chat.app.dto.AuthResponse;
import com.mulkit.chat.app.model.RefreshToken;
import com.mulkit.chat.app.model.User;
import com.mulkit.chat.app.repository.UserRepository;
import com.mulkit.chat.app.security.JwtUtil;
import com.mulkit.chat.app.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {

        if (userRepository.findByEmail(request.getEmail()) != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(false, "Email already registered"));
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponse(true, "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {

        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new AuthResponse(false, "User not found"));
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Wrong password"));
        }

        // generate access token
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // generate refresh token and save to database
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthResponse(true, "Login successful", accessToken, refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {

        // look up the refresh token in the database
        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(request.getRefreshToken());

        // if not found
        if (refreshTokenOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Invalid refresh token"));
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        // if expired
        if (refreshTokenService.isExpired(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Refresh token expired, please login again"));
        }

        // generate a new access token
        String newAccessToken = jwtUtil.generateToken(refreshToken.getUser().getEmail(), refreshToken.getUser().getRole());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthResponse(true, "Token refreshed successfully", newAccessToken, refreshToken.getToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestBody LogoutRequest request) {
        refreshTokenService.deleteByUser(request.getEmail());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthResponse(true, "Logged out successfully"));
    }

    // inner classes for refresh and logout request bodies
    static class RefreshRequest {
        private String refreshToken;
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    static class LogoutRequest {
        private String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}