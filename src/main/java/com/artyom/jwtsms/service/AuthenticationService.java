package com.artyom.jwtsms.service;

import com.artyom.jwtsms.dto.LoginUserDto;
import com.artyom.jwtsms.dto.RegisterUserDto;
import com.artyom.jwtsms.dto.VerifyUserDto;
import com.artyom.jwtsms.entity.User;
import com.artyom.jwtsms.exception.EmailAlreadyExistsException;
import com.artyom.jwtsms.exception.UserNotFoundException;
import com.artyom.jwtsms.exception.VerificationException;
import com.artyom.jwtsms.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public User signup(RegisterUserDto registerUserDto) {

        if (userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new EmailAlreadyExistsException(registerUserDto.getEmail());
        }

        User user = new User(
                registerUserDto.getUsername(),
                registerUserDto.getEmail(),
                passwordEncoder.encode(registerUserDto.getPassword())
        );

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiration(LocalDateTime.now().plusMinutes(15));

        user.setEnabled(false);

        sendVerificationEmail(user);

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto loginUserDto) {
        User user = userRepository.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new VerificationException("Account not verified. Please verify your email");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getEmail(),
                        loginUserDto.getPassword()
                )
        );

        return user;
    }

    public void verifyUser(VerifyUserDto verifyUserDto) {
        Optional<User> optUser = userRepository.findByEmail(verifyUserDto.getEmail());

        if (optUser.isPresent()) {
            User user = optUser.get();
            if (user.getVerificationExpiration().isBefore(LocalDateTime.now())) {
                throw new VerificationException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationExpiration(null);

                userRepository.save(user);
            } else {
                throw new VerificationException("Invalid verification code");
            }
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationExpiration(LocalDateTime.now().plusMinutes(60));

            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Email not found");
        }
    }

    private void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        System.out.println(user);
        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());;
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;

        return String.valueOf(code);
    }
}
