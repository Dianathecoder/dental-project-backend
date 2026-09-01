package com.dynalar.dynalar.service;

import com.dynalar.dynalar.dto.auth.*;
import com.dynalar.dynalar.model.odontogram.Odontogram;
import com.dynalar.dynalar.model.patient.Patient;
import com.dynalar.dynalar.model.user.PasswordResetToken;
import com.dynalar.dynalar.model.user.Role;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.PasswordResetTokenRepository;
import com.dynalar.dynalar.respository.PatientRepository;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordResetTokenRepository resetRepo;
    private final PatientRepository patientRepo; // Añadido para vincular fichas
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public AuthService(UserRepository userRepo,
                       PasswordResetTokenRepository resetRepo,
                       PatientRepository patientRepo,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       JavaMailSender mailSender) {
        this.userRepo = userRepo;
        this.resetRepo = resetRepo;
        this.patientRepo = patientRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    //regitro para la clinica siendo admin
    public AuthResponse registerClinicAdmin(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");

        User user = new User();
        user.setName(req.getName());
        user.setSurname(req.getSurname());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);
        user.setRoles(roles);
        
        userRepo.save(user);

        return new AuthResponse(
                jwtService.generateToken(user),
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getRoles()
        );
    }

    // registro para pde la app siendo pacientes
    public AuthResponse registerPatientApp(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");

        User user = new User();
        user.setName(req.getName());
        user.setSurname(req.getSurname());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        
        Set<Role> roles = new HashSet<>();
        roles.add(Role.PATIENT);
        user.setRoles(roles);
        
        User savedUser = userRepo.save(user);

        // Unir usuario con su ficha médica o crear una nueva
        linkOrCreatePatientRecord(savedUser, req.getName(), req.getSurname(), req.getEmail());

        return new AuthResponse(
                jwtService.generateToken(savedUser),
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getSurname(),
                savedUser.getEmail(),
                savedUser.getRoles()
        );
    }

    // Login con bloqueo
    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));

   
        if (!user.isAccountNonLocked()) {
            if (user.getLockTime() != null && user.getLockTime().plusMinutes(10).isBefore(LocalDateTime.now())) {
            user.setAccountNonLocked(true);
                user.setFailedAttempt(0);
                user.setLockTime(null);
                userRepo.save(user);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                        "Cuenta bloqueada por demasiados intentos. Inténtalo de nuevo en 10 minutos.");
            }
        }

        // Validar contraseña
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            int attempts = user.getFailedAttempt() + 1;
            user.setFailedAttempt(attempts);
            
            if (attempts >= 5) {
                user.setAccountNonLocked(false);
                user.setLockTime(LocalDateTime.now());
                userRepo.save(user);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                        "Has superado el límite de intentos. Cuenta bloqueada durante 10 minutos.");
            }
            
            userRepo.save(user);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                    "Credenciales incorrectas. Intento " + attempts + " de 5.");
        }

     
        if (user.getFailedAttempt() > 0) {
            user.setFailedAttempt(0);
            userRepo.save(user);
        }

        return new AuthResponse(
                jwtService.generateToken(user),
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getRoles()
        );
    }

  
    public AuthResponse googleLogin(String idToken, String type) throws Exception {
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
        String givenName = (String) payload.get("given_name");
        String familyName = (String) payload.get("family_name");

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
                            newUser.setName(givenName != null ? givenName : email);
                            newUser.setSurname(familyName != null ? familyName : "");
                            newUser.setAvatarUrl((String) payload.get("picture"));
                            newUser.setGoogleId(googleId);
                            newUser.setProvider(User.Provider.GOOGLE);
                            newUser.setEmailVerified(true);
                            
                            Set<Role> roles = new HashSet<>();
                            if ("admin".equalsIgnoreCase(type)) {
                                roles.add(Role.ADMIN);
                            } else {
                                roles.add(Role.PATIENT);
                            }
                            newUser.setRoles(roles);           
                            
                            User savedUser = userRepo.save(newUser);

                            // Si se registra como paciente, vinculamos/creamos su ficha médica
                            if ("patient".equalsIgnoreCase(type)) {
                                linkOrCreatePatientRecord(savedUser, newUser.getName(), newUser.getSurname(), email);
                            }

                            return savedUser;
                        }));

        return new AuthResponse(
                jwtService.generateToken(user),
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getRoles()
        );
    }

    // Olvide mi password
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
        message.setSubject("Restablecer contraseña - Dynalar");
        message.setText("Usa este enlace (válido 1 hora):\n\n"
                + frontendUrl + "/reset-password?token=" + token);
        mailSender.send(message);
    }

    //Reset del password
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetRepo.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Token inválido o ya usado"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expirado");

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        

        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        user.setLockTime(null);

        userRepo.save(user);

        resetToken.setUsed(true);
        resetRepo.save(resetToken);
    }


    
    private void linkOrCreatePatientRecord(User savedUser, String name, String surname, String email) {
        Optional<Patient> existingPatient = patientRepo.findByEmail(email);
        
        if (existingPatient.isPresent()) {
            Patient patient = existingPatient.get();
            patient.setUser(savedUser);
            patientRepo.save(patient);
        } else {
            Patient newPatient = new Patient();
            newPatient.setName(name);
            newPatient.setLastName(surname);
            newPatient.setEmail(email);
            newPatient.setUser(savedUser);
            
            Odontogram o = new Odontogram();
            o.setPatient(newPatient);
            newPatient.setOdontogram(o);
            
            patientRepo.save(newPatient);
        }
    }
}