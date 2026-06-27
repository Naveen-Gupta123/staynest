package com.staynest.service;

import com.staynest.dto.ChangePasswordRequest;
import com.staynest.dto.ProfileUpdateRequest;
import com.staynest.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User updateProfile(User user, ProfileUpdateRequest request, MultipartFile profileImage);

    void changePassword(User user, ChangePasswordRequest request);

    List<User> getAllUsers();

    void toggleUserEnabled(Long userId);

    User getUserById(Long userId);
}
