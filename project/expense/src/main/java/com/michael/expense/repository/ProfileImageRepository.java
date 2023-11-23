package com.michael.expense.repository;

import com.michael.expense.entity.ProfileImage;
import com.michael.expense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findProfileImageByUser(User user);
}
