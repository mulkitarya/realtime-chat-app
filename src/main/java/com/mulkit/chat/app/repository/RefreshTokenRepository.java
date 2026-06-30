package com.mulkit.chat.app.repository;

import com.mulkit.chat.app.model.RefreshToken;
import com.mulkit.chat.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Transactional
    void deleteByUser(User user);
}
