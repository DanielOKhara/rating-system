package com.okhara.rating_system.service.security;

import com.okhara.rating_system.exception.AlreadyExistsException;
import com.okhara.rating_system.exception.CoordinationException;
import com.okhara.rating_system.exception.EntityNotExistException;
import com.okhara.rating_system.exception.RefreshTokenException;
import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.RefreshToken;
import com.okhara.rating_system.model.auth.RoleType;
import com.okhara.rating_system.repository.jpa.AppUserRepository;
import com.okhara.rating_system.security.AppUserDetails;
import com.okhara.rating_system.security.jwt.JwtUtils;
import com.okhara.rating_system.service.email.EmailService;
import com.okhara.rating_system.web.dto.request.auth.LoginRequest;
import com.okhara.rating_system.web.dto.request.auth.RefreshTokenRequest;
import com.okhara.rating_system.web.dto.request.auth.RegisterSellerAccountRequest;
import com.okhara.rating_system.web.dto.response.auth.AuthResponse;
import com.okhara.rating_system.web.dto.response.auth.RefreshTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AppUserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final VerificationService verificationService;
    private final ResetPasswordService resetPasswordService;


    @Transactional
    public void registerUser(RegisterSellerAccountRequest request) {

        String normalizedEmail = request.getEmail().toLowerCase();
        log.debug("Started register of new user {}", request.getNickname());

        if(userRepository.existsByNicknameIgnoreCase(request.getNickname())){
            throw new AlreadyExistsException(
                    MessageFormat.format("Nickname {0} already in use", request.getNickname()
            ));
        }
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new AlreadyExistsException(
                    MessageFormat.format("Email {0} already in use", normalizedEmail));
        }

        AppUser newUser = AppUser.builder()
                .nickname(request.getNickname())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(EnumSet.of(RoleType.ROLE_SELLER))
                .status(AccountStatus.PENDING)
                .build();

        userRepository.save(newUser);

        log.debug("User {} registered with rating {}", request.getNickname(), newUser.getRating());

        String verificationLink = verificationService.generateLink(newUser);

        emailService.sendVerificationEmail(newUser.getEmail(), verificationLink);
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return AuthResponse.builder()
                .id(userDetails.getId())
                .token(jwtUtils.generateJwtToken(userDetails))
                .refreshToken(refreshToken.getToken())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request){
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByRefreshToken(requestRefreshToken)
                .map(refreshTokenService::checkRefreshToken)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    AppUser tokenOwner = userRepository.findById(userId).orElseThrow(() ->
                            new RefreshTokenException("Exception trying to get token for userId: " + userId));
                    String token = jwtUtils.generateTokenFromUsername(tokenOwner.getNickname());

                    return new RefreshTokenResponse(token, refreshTokenService.createRefreshToken(userId).getToken());
                }).orElseThrow(() -> new RefreshTokenException(requestRefreshToken, "Refresh token not found"));
    }

    public void changePasswordRequest(String email){
        AppUser appUser = userRepository.findByEmailAndStatusIn(email.toLowerCase(), Set.of(AccountStatus.ACTIVE))
                .orElseThrow(() -> new EntityNotExistException(MessageFormat.format(
                        "User with email \"{0}\" not exist", email
                )));

        String link = resetPasswordService.generateLink(appUser);
        emailService.sendResetPasswordEmail(appUser.getEmail(), link);
    }

    @Transactional
    public void changePassword(String code, String password){
        Long userId = resetPasswordService.getUserIdIfActual(code);
        AppUser user = userRepository.findByIdAndStatus(userId, AccountStatus.ACTIVE).orElseThrow(
                () -> new EntityNotExistException("User not exist or not activated yet"));
        String encodedNewPassword = passwordEncoder.encode(password);
        if(passwordEncoder.matches(password, user.getPassword())){
            throw new CoordinationException("New password and previous can't be same");
        }

        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        resetPasswordService.deleteResetPasswordCode(userId);
    }

    public boolean isActiveResetCode(String code){
        resetPasswordService.getUserIdIfActual(code);
        return true;
    }

    public void logout(){
        var currentPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(currentPrincipal instanceof AppUserDetails userDetails){
            Long userId = userDetails.getId();

            refreshTokenService.deleteByUserId(userId);
        }
    }

    public void verifyAccount(String code){
        Long verifiedUserId = verificationService.verifyUserAndGetIdIfSuccess(code);

        AppUser verifiedUser = userRepository.findById(verifiedUserId).orElseThrow(()->
                new EntityNotExistException("User doesn't exist"));
        verifiedUser.setEmailVerified(true);
        userRepository.save(verifiedUser);
    }
}
