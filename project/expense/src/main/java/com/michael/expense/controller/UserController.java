package com.michael.expense.controller;


import com.michael.expense.payload.request.UserRequest;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.payload.response.UserPaginationResponse;
import com.michael.expense.payload.response.UserResponse;
import com.michael.expense.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.michael.expense.constant.PaginationConstants.*;
import static com.michael.expense.constant.PaginationConstants.DEFAULT_SORT_DIRECTION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRequest userRequest) {
        return new ResponseEntity<>(userService.createUser(userRequest), CREATED);
    }

    @GetMapping("/user/get/my_profile")
    public ResponseEntity<UserResponse> getUserProfile() {
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

}
