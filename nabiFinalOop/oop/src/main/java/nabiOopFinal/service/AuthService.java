package nabiOopFinal.service;

import nabiOopFinal.dto.AuthRequest;
import nabiOopFinal.dto.AuthResponse;
import nabiOopFinal.dto.RegisterRequest;
import nabiOopFinal.dto.RegisterResponse;
import nabiOopFinal.exception.AuthenticationException;
import nabiOopFinal.exception.NotFoundException;
import nabiOopFinal.model.Token;
import nabiOopFinal.model.User;
import nabiOopFinal.model.enums.TokenType;
import nabiOopFinal.repository.TokenRepository;
import nabiOopFinal.repository.UserRepository;
import nabiOopFinal.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public RegisterResponse register(RegisterRequest request) {
        logger.info("Registration initiated for username: {}", request.getUsername());

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);
        logger.info("Registration successful for username: {}", request.getUsername());

        return RegisterResponse.builder()
                .username(request.getUsername())
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            logger.info("Login successful for email: {}", request.getEmail());
        } catch (AuthenticationException ex) {
            throw new AuthenticationException("Authentication failed: " + ex.getMessage());
        }
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + request.getEmail()));
        var jwtToken = jwtService.generateToken(user);
//        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthResponse.builder()
                .email(user.getEmail())
                .accessToken(jwtToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

}

