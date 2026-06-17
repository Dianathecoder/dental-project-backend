package com.dynalar.dynalar.respository;

import com.dynalar.dynalar.model.user.PasswordResetToken;
import com.dynalar.dynalar.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
    void deleteByUser(User user);
}