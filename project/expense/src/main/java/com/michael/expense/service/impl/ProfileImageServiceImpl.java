package com.michael.expense.service.impl;


import com.michael.expense.entity.ProfileImage;
import com.michael.expense.entity.User;
import com.michael.expense.exceptions.payload.ImageNotFoundException;
import com.michael.expense.exceptions.payload.UserNotFoundException;
import com.michael.expense.payload.response.UserResponse;
import com.michael.expense.repository.ProfileImageRepository;
import com.michael.expense.repository.UserRepository;
import com.michael.expense.service.ProfileImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static com.michael.expense.constant.FileConstant.*;
import static com.michael.expense.constant.UserConstant.NO_USER_FOUND_BY_USERNAME;
import static org.springframework.http.MediaType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageServiceImpl implements ProfileImageService {

    private final ProfileImageRepository profileImageRepository;
    private final ModelMapper mapper;
    private final UserRepository userRepository;

    @Override
    public ProfileImage saveTempProfileImage(User user) throws IOException {
        ProfileImage profileImage = ProfileImage.builder()
                .user(user)
                .fileName(user.getUsername() + DOT + JPG_EXTENSION)
                .fileType(IMAGE_JPEG_VALUE)
                .data(compressImage(getDefaultImage(user.getUsername())))
                .profileImageURL(setProfileImageUrl(user.getUsername()))
                .build();
        profileImage = profileImageRepository.save(profileImage);

        log.info("Saved Profile Image in database by name: {}", profileImage.getFileName());
        return profileImage;
    }

    @Override
    public byte[] getProfileImage(String username, String fileName) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(String.format(NO_USER_FOUND_BY_USERNAME, username)));
        if (user.getProfileImage() != null && fileName.equals(user.getProfileImage().getFileName())) {
            return decompressImage(user.getProfileImage().getData());
        }
        throw new ImageNotFoundException(IMAGE_NOT_FOUND);
    }


    @Override
    public UserResponse updateProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
                throw new RuntimeException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }
            if (user.getProfileImage().getProfileImageURL() != null) {
                ProfileImage imageDataDb = findProfileImageInDB(user);
                imageDataDb.setData(compressImage(profileImage.getBytes()));
                imageDataDb.setFileType(profileImage.getContentType());
                profileImageRepository.save(imageDataDb);
            }
            log.info("Saved file in database by name: {}", profileImage.getOriginalFilename());
            return mapper.map(user, UserResponse.class);
        } else {
            throw new ImageNotFoundException(IMAGE_NOT_FOUND);
        }
    }

    @Override
    public UserResponse deleteProfileImageAndSetDefaultImage(User user) throws IOException {
        ProfileImage profileImageInDB = findProfileImageInDB(user);
        profileImageInDB.setData(compressImage(getDefaultImage(user.getUsername())));
        profileImageInDB.setFileType(IMAGE_JPEG_VALUE);
        profileImageRepository.save(profileImageInDB);
        return mapper.map(user, UserResponse.class);
    }


    private ProfileImage findProfileImageInDB(User user) {
        return profileImageRepository
                .findProfileImageByUser(user)
                .orElseThrow(() -> new ImageNotFoundException(IMAGE_NOT_FOUND));
    }


    private byte[] compressImage(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }

    private byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }

    private byte[] getDefaultImage(String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(PATH_PREFIX + USER_IMAGE_PATH + username + FORWARD_SLASH
                        + username + DOT + JPG_EXTENSION).toUriString();
    }

}
