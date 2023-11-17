package com.michael.expense.service.impl;

import com.michael.expense.entity.EmailConfirmationToken;
import com.michael.expense.repository.EmailConfirmationTokenRepository;
import com.michael.expense.repository.UserRepository;
import com.michael.expense.service.EmailConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.michael.expense.constant.SecurityConstant.VERIFICATION_TOKEN_EXPIRED;
import static com.michael.expense.constant.SecurityConstant.VERIFICATION_TOKEN_NOT_FOUND;
import static com.michael.expense.constant.UserConstant.CONFIRMED;
import static com.michael.expense.constant.UserConstant.EMAIL_ALREADY_CONFIRMED;

@RequiredArgsConstructor
@Service
public class EmailConfirmationTokenServiceImpl implements EmailConfirmationTokenService {

    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    private final UserRepository userRepository;

    @Override
    public void saveConfirmationToken(EmailConfirmationToken token) {
        emailConfirmationTokenRepository.save(token);
    }

    @Transactional
    @Override
    public String confirmToken(String token) {
        EmailConfirmationToken confirmationToken = emailConfirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException(VERIFICATION_TOKEN_NOT_FOUND));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new RuntimeException(EMAIL_ALREADY_CONFIRMED);
        }
        LocalDateTime expiredDate = confirmationToken.getExpiredAt();
        if (expiredDate.isBefore(LocalDateTime.now())){
            throw  new RuntimeException(VERIFICATION_TOKEN_EXPIRED);
        }
        emailConfirmationTokenRepository.updateConfirmedDate(token, LocalDateTime.now());
        userRepository.enableUser(confirmationToken.getUser().getEmail());
        return CONFIRMED;
    }
}
