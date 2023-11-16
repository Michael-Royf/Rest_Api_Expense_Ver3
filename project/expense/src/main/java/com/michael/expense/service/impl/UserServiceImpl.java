package com.michael.expense.service.impl;

import com.michael.expense.entity.User;
import com.michael.expense.entity.enumerations.UserRole;
import com.michael.expense.exceptions.payload.EmailExistException;
import com.michael.expense.exceptions.payload.UserNotFoundException;
import com.michael.expense.exceptions.payload.UsernameExistException;
import com.michael.expense.payload.request.LoginRequest;
import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.JwtAuthResponse;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.payload.response.UserResponse;
import com.michael.expense.repository.UserRepository;
import com.michael.expense.security.JwtTokenProvider;
import com.michael.expense.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.michael.expense.constant.UserConstant.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponse createUser(UserRequest userRequest) {

        validateNewUsernameAndEmail(
                StringUtils.EMPTY,
                userRequest.getUsername().trim(),
                userRequest.getEmail().trim().toLowerCase());

        User user = User.builder()
                .username(userRequest.getUsername())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(UserRole.ROLE_USER)
                .build();
        user = userRepository.save(user);
        return mapper.map(user, UserResponse.class);
    }

    @Override
    public JwtAuthResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String username = authenticate.getName();
        //   User user = findUserByUsernameInDB(username);

        String jwtAccessToken = jwtTokenProvider.generateAccessToken(username);
        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(username);

//        Token token = createTokenForDB(user, jwtAccessToken);
//        revokeAllUserTokens(user);
//        tokenRepository.save(token);
        return JwtAuthResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    @Override
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return findUserByUsernameInDB(username);
    }

    @Override
    public UserResponse getMyProfile() {
        return mapper.map(getLoggedInUser(), UserResponse.class);
    }


    @Override
    public UserResponse getUserById(Long userId) {
        return mapper.map(findUserInDBById(userId), UserResponse.class);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return mapper.map(findUserByEmailInDB(email), UserResponse.class);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        return mapper.map(findUserByUsernameInDB(username), UserResponse.class);
    }


    //todo: Pagination
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> mapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(UserRequest userRequest) {
        User user = getLoggedInUser();

        validateNewUsernameAndEmail(
                user.getUsername(),
                userRequest.getUsername().trim(),
                userRequest.getEmail().trim().toLowerCase());

        user.setUsername(userRequest.getUsername());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user = userRepository.save(user);
        return mapper.map(user, UserResponse.class);
    }

    @Override
    public MessageResponse deleteUserProfile() {
        User user = getLoggedInUser();
        userRepository.delete(user);
        return new MessageResponse(String.format(USER_DELETED, user.getUsername()));
    }


    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findOptionalUserByUsername(newUsername).orElse(null);
        User userByNewEmail = findOptionalUserByEmail(newEmail).orElse(null);
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findOptionalUserByUsername(currentUsername).orElse(null);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    private User findUserInDBById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(NO_USER_FOUND_BY_ID, userId)));
    }

    private Optional<User> findOptionalUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    private User findUserByUsernameInDB(String username) {
        return findOptionalUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username));
    }


    private Optional<User> findOptionalUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    private User findUserByEmailInDB(String email) {
        return findOptionalUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(NO_USER_FOUND_BY_EMAIL, email)));
    }


}
