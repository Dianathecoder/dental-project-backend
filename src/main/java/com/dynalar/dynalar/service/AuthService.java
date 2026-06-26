package com.dynalar.dynalar.service;

import com.dynalar.dynalar.dto.auth.*;
import com.dynalar.dynalar.model.user.PasswordResetToken;
import com.dynalar.dynalar.model.user.Role;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.PasswordResetTokenRepository;
import com.dynalar.dynalar.respository.UserRepository;
import com.dynalar.dynalar.security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordResetTokenRepository resetRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public AuthService(UserRepository userRepo,
                       PasswordResetTokenRepository resetRepo,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       JavaMailSender mailSender) {
        this.userRepo = userRepo;
        this.resetRepo = resetRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    // REGISTRO
    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");

        User user = new User();
        user.setName(req.getName());
        user.setSurname(req.getSurname());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.USER);
        userRepo.save(user);

        return new AuthResponse(
                jwtService.generateToken(user),
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getRole()
        );
    }

    // LOGIN
    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");

        return new AuthResponse(
                jwtService.generateToken(user),
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getRole()
        );
    }

    // GOOGLE LOGIN
    public AuthResponse googleLogin(String idToken) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de Google inválido");

        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        User user = userRepo.findByGoogleId(googleId)
                .orElseGet(() -> userRepo.findByEmail(email)
                        .map(u -> {
                            u.setGoogleId(googleId);
                            u.setProvider(User.Provider.GOOGLE);
                            return userRepo.save(u);
                        })
                        .orElseGet(() -> {
                            User newUser = new User();
                            newUser.setEmail(email);
                            newUser.setName(name != null ? name : email);
                            newUser.setAvatarUrl(picture);
                            newUser.setGoogleId(googleId);
                            newUser.setProvider(User.Provider.GOOGLE);
                            newUser.setEmailVerified(true);
                            newUser.setRole(Role.USER);
                            return userRepo.save(newUser);
                        }));

        return new AuthResponse(
                jwtService.generateToken(user),
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getRole()
        );
    }

    // OLVIDÉ CONTRASEÑA
    public void forgotPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Email no encontrado"));

        resetRepo.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        resetRepo.save(resetToken);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Restablecer contraseña - Dental Aux");
        message.setText("Usa este enlace (válido 1 hora):\n\n"
                + frontendUrl + "/reset-password?token=" + token);
        mailSender.send(message);
    }

    // RESET CONTRASEÑA
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetRepo.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Token inválido o ya usado"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expirado");

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        resetToken.setUsed(true);
        resetRepo.save(resetToken);
    }
}