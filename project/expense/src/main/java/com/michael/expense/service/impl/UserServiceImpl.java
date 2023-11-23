package com.michael.expense.service.impl;

import com.michael.expense.entity.EmailConfirmationToken;
import com.michael.expense.entity.ProfileImage;
import com.michael.expense.entity.User;
import com.michael.expense.entity.enumerations.UserRole;
import com.michael.expense.exceptions.payload.EmailExistException;
import com.michael.expense.exceptions.payload.UserNotFoundException;
import com.michael.expense.exceptions.payload.UsernameExistException;
import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.payload.response.UserPaginationResponse;
import com.michael.expense.payload.response.UserResponse;
import com.michael.expense.repository.UserRepository;
import com.michael.expense.service.EmailConfirmationTokenService;
import com.michael.expense.service.EmailSender;
import com.michael.expense.service.ProfileImageService;
import com.michael.expense.service.UserService;
import com.michael.expense.utility.EmailBuilder;
import com.michael.expense.utility.RandomUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.michael.expense.constant.SecurityConstant.LINK_FOR_CONFIRMATION;
import static com.michael.expense.constant.UserConstant.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional //!!
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailConfirmationTokenService emailConfirmationTokenService;
    private final EmailBuilder emailBuilder;
    private final RandomUtils randomUtils;
    private final EmailSender emailSender;
    private final ProfileImageService profileImageService;

    @SneakyThrows
    @Override
    public String createUser(UserRequest userRequest) {
        validateNewUsernameAndEmail(
                StringUtils.EMPTY,
                userRequest.getUsername().trim(),
                userRequest.getEmail().trim().toLowerCase());

        User user = User.builder()
                .username(userRequest.getUsername())
                .firstName(firstLetterUpper(userRequest.getFirstName()))
                .lastName(firstLetterUpper(userRequest.getLastName()))
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .userRole(UserRole.ROLE_USER)
                .isNotLocked(true)
                .build();
        user = userRepository.save(user);

        ProfileImage profileImage = profileImageService.saveTempProfileImage(user);
        user.setProfileImage(profileImage);
        user = userRepository.save(user);

        verificationEmail(user);

        return "User registered successfully! \n" +
                "Check your email address.";
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

    @Override
    public UserPaginationResponse getAllUsers(int pageNo, int pageSiZe, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSiZe, sortBy, sortDir);
        Page<User> users = userRepository.findAll(pageable);

        List<UserResponse> userResponses = users.stream()
                .map(user -> mapper.map(user, UserResponse.class))
                .collect(Collectors.toList());

        return UserPaginationResponse.builder()
                .content(userResponses)
                .pageNo(users.getNumber())
                .pageSize(users.getSize())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .last(users.isLast())
                .build();
    }

    @Override
    public UserResponse updateUser(UserRequest userRequest) {
        User user = getLoggedInUser();

        validateNewUsernameAndEmail(
                user.getUsername(),
                userRequest.getUsername().trim(),
                userRequest.getEmail().trim().toLowerCase());

        user.setUsername(userRequest.getUsername());
        user.setFirstName(firstLetterUpper(userRequest.getFirstName()));
        user.setLastName(firstLetterUpper(userRequest.getLastName()));
        user = userRepository.save(user);
        return mapper.map(user, UserResponse.class);
    }

    @Override
    public MessageResponse deleteUserProfile() {
        User user = getLoggedInUser();
        userRepository.delete(user);
        return new MessageResponse(String.format(USER_DELETED, user.getUsername()));
    }

    private void verificationEmail(User user) {
        String token = randomUtils.generateTokenForEmailVerification();
        EmailConfirmationToken confirmationToken = EmailConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        emailConfirmationTokenService.saveConfirmationToken(confirmationToken);
        String link = LINK_FOR_CONFIRMATION + token;
        emailSender.sendEmailForVerification(
                user.getEmail(),
                emailBuilder.buildEmailForConfirmationEmail(user.getFirstName(), link)
        );
    }


    private void validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
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
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
        }
    }

    private User findUserInDBById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(NO_USER_FOUND_BY_ID, userId)));
    }

    private Optional<User> findOptionalUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    protected User findUserByUsernameInDB(String username) {
        return findOptionalUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username));
    }

    private Optional<User> findOptionalUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    protected User findUserByEmailInDB(String email) {
        return findOptionalUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(NO_USER_FOUND_BY_EMAIL, email)));
    }

    private String firstLetterUpper(String world) {
        String[] words = world.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                char firstChar = Character.toUpperCase(word.charAt(0));
                String capitalizedWord = firstChar + word.substring(1).toLowerCase();
                result.append(capitalizedWord).append(" ");
            }
        }
        return result.toString().trim();
    }

    private Pageable createPageable(int pageNo, int pageSiZe, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSiZe, sort);
    }

}
