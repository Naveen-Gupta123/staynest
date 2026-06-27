package com.staynest.service.impl;

import com.staynest.dto.ChangePasswordRequest;
import com.staynest.dto.ProfileUpdateRequest;
import com.staynest.entity.User;
import com.staynest.exception.ResourceNotFoundException;
import com.staynest.repository.UserRepository;
import com.staynest.service.UserService;
import com.staynest.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageUtil fileStorageUtil;

    @Override
    @Transactional
    public User updateProfile(User user, ProfileUpdateRequest request, MultipartFile profileImage) {
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setBio(request.getBio());

        if (profileImage != null && !profileImage.isEmpty()) {
            String url = fileStorageUtil.storeFile(profileImage);
            user.setProfileImage(url);
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        // Re-fetch fresh from DB so we're checking against the real stored hash.
        User dbUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), dbUser.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match.");
        }

        dbUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(dbUser);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void toggleUserEnabled(Long userId) {
        User user = getUserById(userId);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
