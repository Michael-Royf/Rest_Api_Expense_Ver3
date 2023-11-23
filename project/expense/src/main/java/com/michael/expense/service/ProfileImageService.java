package com.michael.expense.service;


import com.michael.expense.entity.ProfileImage;
import com.michael.expense.entity.User;
import com.michael.expense.payload.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileImageService {
    ProfileImage saveTempProfileImage(User user) throws IOException;

    byte[] getProfileImage(String username, String fileName);

    UserResponse updateProfileImage(User user, MultipartFile profileImage) throws IOException;

    UserResponse deleteProfileImageAndSetDefaultImage(User user) throws IOException;
}
