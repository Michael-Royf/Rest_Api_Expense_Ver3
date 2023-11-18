package com.michael.expense.service.impl;

import com.michael.expense.entity.JWTToken;
import com.michael.expense.entity.User;
import com.michael.expense.entity.enumerations.TokenType;
import com.michael.expense.exceptions.payload.TokenException;
import com.michael.expense.payload.request.ChangePasswordRequest;
import com.michael.expense.payload.request.LoginRequest;
import com.michael.expense.payload.response.JwtAuthResponse;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.repository.JwtTokenRepository;
import com.michael.expense.repository.UserRepository;
import com.michael.expense.security.JwtTokenProvider;
import com.michael.expense.service.AuthService;
import com.michael.expense.service.EmailSender;
import com.michael.expense.utility.RandomUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.michael.expense.constant.SecurityConstant.REFRESH_TOKEN_NOT_FOUND_OR_EXPIRED;
import static com.michael.expense.constant.SecurityConstant.TOKEN_PREFIX;

import static com.michael.expense.constant.UserConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenRepository jwtTokenRepository;
    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RandomUtils randomUtils;
    private final EmailSender emailSender;




    @Override
    public JwtAuthResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String username = authenticate.getName();
        User user = userService.findUserByUsernameInDB(username);

        String jwtAccessToken = jwtTokenProvider.generateAccessToken(username);
        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        JWTToken jwtToken = createTokenForDB(user, jwtAccessToken);
        revokeAllUserJWTTokens(user);
        jwtTokenRepository.save(jwtToken);
        return JwtAuthResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }


    @Override
    public JwtAuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getTokenFromRequest(request);
        if (org.springframework.util.StringUtils.hasText(refreshToken)
                && jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsername(refreshToken);

            User user = userService.findUserByUsernameInDB(username);
            String accessToken = jwtTokenProvider.generateAccessToken(username);
            revokeAllUserJWTTokens(user);
            JWTToken jwtToken = createTokenForDB(user, accessToken);
            jwtTokenRepository.save(jwtToken);
            return JwtAuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        throw new TokenException(REFRESH_TOKEN_NOT_FOUND_OR_EXPIRED);
    }


    @Override
    public MessageResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = userService.getLoggedInUser();
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException(PASSWORD_ENTERED_INCORRECTLY);
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        return new MessageResponse(PASSWORD_WAS_UPDATED);
    }

    @Override
    public MessageResponse resetPassword(String email) {
        User user  = userService.findUserByEmailInDB(email);
        String newPassword = randomUtils.generatePassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        emailSender.sendNewPassword(email, user.getFirstName(), newPassword);
        return new MessageResponse(NEW_PASSWORD_SEND_TO_EMAIL);
    }


    private JWTToken createTokenForDB(User user, String accessToken) {
        return JWTToken.builder()
                .user(user)
                .token(accessToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
    }

    private void revokeAllUserJWTTokens(User user) {
        List<JWTToken> validUserTokens = jwtTokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        jwtTokenRepository.saveAll(validUserTokens);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (org.springframework.util.StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }


}
