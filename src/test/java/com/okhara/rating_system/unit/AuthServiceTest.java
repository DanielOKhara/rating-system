package com.okhara.rating_system.unit;

import com.okhara.rating_system.exception.AlreadyExistsException;
import com.okhara.rating_system.model.auth.*;
import com.okhara.rating_system.repository.jpa.AppUserRepository;
import com.okhara.rating_system.security.AppUserDetails;
import com.okhara.rating_system.security.jwt.JwtUtils;
import com.okhara.rating_system.service.email.EmailService;
import com.okhara.rating_system.service.security.AuthService;
import com.okhara.rating_system.service.security.RefreshTokenService;
import com.okhara.rating_system.service.security.ResetPasswordService;
import com.okhara.rating_system.service.security.VerificationService;
import com.okhara.rating_system.web.dto.request.auth.*;
import com.okhara.rating_system.web.dto.response.auth.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private EmailService emailService;
    @Mock
    private VerificationService verificationService;
    @Mock
    private ResetPasswordService resetPasswordService;
    @InjectMocks
    private AuthService authService;

    private RegisterSellerAccountRequest registerRequest;
    private LoginRequest loginRequest;
    private AppUser testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterSellerAccountRequest("testNick", "password", "test@example.com");
        loginRequest = new LoginRequest("testNick", "password");

        testUser = AppUser.builder()
                .nickname("testNick")
                .email("test@example.com")
                .password("encodedPassword")
                .roles(Set.of(RoleType.ROLE_SELLER))
                .status(AccountStatus.PENDING)
                .build();
    }

    @Test
    void registerUser_shouldThrowExceptionIfNicknameExists() {
        when(userRepository.existsByNicknameIgnoreCase(registerRequest.getNickname())).thenReturn(true);
        assertThrows(AlreadyExistsException.class, () -> authService.registerUser(registerRequest));
    }

    @Test
    void registerUser_shouldThrowExceptionIfEmailExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail().toLowerCase())).thenReturn(true);
        assertThrows(AlreadyExistsException.class, () -> authService.registerUser(registerRequest));
    }

    @Test
    void registerUser_shouldSaveUserAndSendEmail() {
        when(userRepository.existsByNicknameIgnoreCase(registerRequest.getNickname())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail().toLowerCase())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(testUser);
        when(verificationService.generateLink(any())).thenReturn("verificationLink");

        authService.registerUser(registerRequest);

        verify(userRepository).save(any(AppUser.class));

        verify(emailService).sendVerificationEmail(eq("test@example.com"), eq("verificationLink"));
    }

    @Test
    void authenticateUser_shouldReturnAuthResponse() {
        Authentication authentication = mock(Authentication.class);
        AppUserDetails userDetails = mock(AppUserDetails.class);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("testNick");
        when(userDetails.getEmail()).thenReturn("test@example.com");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(refreshToken);

        AuthResponse response = authService.authenticateUser(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void refreshToken_shouldReturnNewTokens() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");
        refreshToken.setUserId(1L);

        AppUser testUser = AppUser.builder()
                .id(1L)
                .nickname("testNick")
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        when(refreshTokenService.findByRefreshToken(request.getRefreshToken())).thenReturn(java.util.Optional.of(refreshToken));
        when(refreshTokenService.checkRefreshToken(refreshToken)).thenReturn(refreshToken);
        when(userRepository.findById(refreshToken.getUserId())).thenReturn(java.util.Optional.of(testUser));
        when(jwtUtils.generateTokenFromUsername(testUser.getNickname())).thenReturn("new-jwt-token");
        when(refreshTokenService.createRefreshToken(refreshToken.getUserId())).thenReturn(refreshToken);

        RefreshTokenResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("new-jwt-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void changePasswordRequest_shouldSendEmailIfUserExists() {
        String email = "test@example.com";
        AppUser testUser = AppUser.builder()
                .id(1L)
                .email(email)
                .status(AccountStatus.ACTIVE)
                .build();

        when(userRepository.findByEmailAndStatusIn(email.toLowerCase(), Set.of(AccountStatus.ACTIVE)))
                .thenReturn(java.util.Optional.of(testUser));
        when(resetPasswordService.generateLink(testUser)).thenReturn("reset-link");

        authService.changePasswordRequest(email);

        verify(emailService).sendResetPasswordEmail(email, "reset-link");
    }

    @Test
    void changePassword_shouldUpdatePasswordAndDeleteResetCode() {
        String code = "reset-code";
        String newPassword = "newPassword";
        Long userId = 1L;

        AppUser testUser = AppUser.builder()
                .id(userId)
                .password("oldEncodedPassword")
                .status(AccountStatus.ACTIVE)
                .build();

        when(resetPasswordService.getUserIdIfActual(code)).thenReturn(userId);
        when(userRepository.findByIdAndStatus(userId, AccountStatus.ACTIVE)).thenReturn(java.util.Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(passwordEncoder.matches(newPassword, testUser.getPassword())).thenReturn(false);

        authService.changePassword(code, newPassword);

        assertEquals("newEncodedPassword", testUser.getPassword());
        verify(userRepository).save(testUser);
        verify(resetPasswordService).deleteResetPasswordCode(userId);
    }

    @Test
    void isActiveResetCode_shouldReturnTrueIfCodeIsValid() {
        String code = "valid-reset-code";
        when(resetPasswordService.getUserIdIfActual(code)).thenReturn(1L);

        assertTrue(authService.isActiveResetCode(code));
    }
}