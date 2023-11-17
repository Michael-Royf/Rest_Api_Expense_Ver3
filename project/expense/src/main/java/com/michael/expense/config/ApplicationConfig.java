package com.michael.expense.config;

import com.michael.expense.constant.UserConstant;
import com.michael.expense.entity.User;
import com.michael.expense.exceptions.payload.UserNotFoundException;
import com.michael.expense.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static com.michael.expense.constant.UserConstant.NO_USER_FOUND_BY_EMAIL_OR_USERNAME;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return usernameOrEmail -> {
            User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                    .orElseThrow(() -> new UserNotFoundException(String.format(NO_USER_FOUND_BY_EMAIL_OR_USERNAME, usernameOrEmail)));
            user.setLastLoginDate(LocalDateTime.now());
            user = userRepository.save(user);
            return user;
        };
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
