package com.staynest.service.impl;

import com.staynest.dto.RegisterRequest;
import com.staynest.entity.Role;
import com.staynest.entity.User;
import com.staynest.exception.DuplicateResourceException;
import com.staynest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest();
        validRequest.setFullName("Test User");
        validRequest.setEmail("newuser@example.com");
        validRequest.setPassword("Password123");
        validRequest.setConfirmPassword("Password123");
        validRequest.setRole("GUEST");
    }

    @Test
    void register_hashesPasswordBeforeSaving_neverStoresPlainText() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("HASHED_VALUE_NOT_PLAINTEXT");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = authService.register(validRequest);

        assertEquals("HASHED_VALUE_NOT_PLAINTEXT", saved.getPassword());
        assertNotEquals("Password123", saved.getPassword());
        verify(passwordEncoder, times(1)).encode("Password123");
    }

    @Test
    void register_throwsDuplicateResourceException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(validRequest));

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsIllegalArgumentException_whenPasswordsDontMatch() {
        validRequest.setConfirmPassword("DifferentPassword");
        when(userRepository.existsByEmail(any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.register(validRequest));

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsIllegalArgumentException_whenTryingToSelfRegisterAsAdmin() {
        validRequest.setRole("ADMIN");
        when(userRepository.existsByEmail(any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.register(validRequest));

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_assignsGuestRole_whenRoleIsGuest() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = authService.register(validRequest);

        assertEquals(Role.GUEST, saved.getRole());
    }
}
