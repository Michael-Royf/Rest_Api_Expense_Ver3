package com.michael.expense.controller;


import com.michael.expense.entity.User;
import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.payload.response.UserPaginationResponse;
import com.michael.expense.payload.response.UserResponse;
import com.michael.expense.service.ProfileImageService;
import com.michael.expense.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.michael.expense.constant.PaginationConstants.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProfileImageService profileImageService;

    @PostMapping("/user/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRequest userRequest) {
        return new ResponseEntity<>(userService.createUser(userRequest), CREATED);
    }

    @GetMapping("/user/get/my_profile")
    public ResponseEntity<UserResponse> getMyUserProfile() {
        return new ResponseEntity<>(userService.getMyProfile(), OK);
    }

    @GetMapping("/user/get/id/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), OK);
    }

    @GetMapping("/user/get/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable("email") String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email), OK);
    }

    @GetMapping("/user/get/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable("username") String username) {
        return new ResponseEntity<>(userService.getUserByUsername(username), OK);
    }

    @GetMapping("/user/get/allUsers")
    public ResponseEntity<UserPaginationResponse> getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return new ResponseEntity<>(userService.getAllUsers(page, pageSize, sortBy, sortDir), OK);
    }


    @PutMapping("/user/update_profile")
    public ResponseEntity<UserResponse> updateUser(@RequestBody @Valid UserRequest userRequest) {
        return new ResponseEntity<>(userService.updateUser(userRequest), OK);
    }

    @DeleteMapping("/user/remove_profile")
    public ResponseEntity<MessageResponse> removeUser() {
        return new ResponseEntity<>(userService.deleteUserProfile(), OK);
    }


    @GetMapping(path = "/user/image/{username}/{filename}", produces = IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getProfileImage(@PathVariable("username") String username,
                                             @PathVariable("filename") String fileName) {
        byte[] profileImage = profileImageService.getProfileImage(username, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(IMAGE_JPEG_VALUE))
                .body(new ByteArrayResource(profileImage));
    }

    @PostMapping("/user/update_profile_image")
    public ResponseEntity<UserResponse> updateProfileImage(@RequestParam(value = "profileImage") MultipartFile profileImage) throws IOException {
        User user = userService.getLoggedInUser();
        return new ResponseEntity<>(profileImageService.updateProfileImage(user, profileImage), OK);
    }

    @DeleteMapping("/user/delete_profile_image")
    public ResponseEntity<UserResponse> deleteProfileImage() throws IOException {
        User user = userService.getLoggedInUser();
        return new ResponseEntity<>(profileImageService.deleteProfileImageAndSetDefaultImage(user), OK);
    }

}
