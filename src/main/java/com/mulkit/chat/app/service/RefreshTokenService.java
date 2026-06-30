package com.mulkit.chat.app.service;

import com.mulkit.chat.app.model.RefreshToken;
import com.mulkit.chat.app.model.User;
import com.mulkit.chat.app.repository.RefreshTokenRepository;
import com.mulkit.chat.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final long REFRESH_EXPIRATION_MS = 604_800_000;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public RefreshToken createRefreshToken(String email){
        User user = userRepository.findByEmail(email);

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_EXPIRATION_MS));

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isExpired(RefreshToken token){
        return token.getExpiryDate().isBefore(Instant.now());
    }

    @Transactional
    public void deleteByUser(String email){
        User user = userRepository.findByEmail(email);
        refreshTokenRepository.deleteByUser(user);
    }
}
