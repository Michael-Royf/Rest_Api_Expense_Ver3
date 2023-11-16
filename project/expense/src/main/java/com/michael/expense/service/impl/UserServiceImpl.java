package com.michael.expense.service.impl;

import com.michael.expense.entity.User;
import com.michael.expense.exceptions.payload.EmailExistException;
import com.michael.expense.exceptions.payload.UserNotFoundException;
import com.michael.expense.exceptions.payload.UsernameExistException;
import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.payload.response.UserResponse;
import com.michael.expense.repository.UserRepository;
import com.michael.expense.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
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
                .password(userRequest.getPassword())
                .build();
        user = userRepository.save(user);
        return mapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return mapper.map(findUserInDBById(userId), UserResponse.class);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = findOptionalUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(NO_USER_FOUND_BY_EMAIL, email)));
        return mapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = findOptionalUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format(NO_USER_FOUND_BY_USERNAME, username)));
        return mapper.map(user, UserResponse.class);
    }


    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> mapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long userId, UserRequest userRequest) {
        User user = findUserInDBById(userId);

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
    public MessageResponse deleteUserById(Long userId) {
        User user = findUserInDBById(userId);
        userRepository.delete(user);
        return new MessageResponse(String.format(USER_DELETED, userId));
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


    private Optional<User> findOptionalUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

}
