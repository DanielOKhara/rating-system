package com.okhara.rating_system.web.controller.open;

import com.okhara.rating_system.service.security.AuthService;
import com.okhara.rating_system.web.dto.request.auth.*;
import com.okhara.rating_system.web.dto.response.auth.AuthResponse;
import com.okhara.rating_system.web.dto.response.auth.RefreshTokenResponse;
import com.okhara.rating_system.web.dto.response.SimpleResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authUser(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.accepted().body(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<SimpleResponse> registerUser(@RequestBody @Valid RegisterSellerAccountRequest registerRequest){
        authService.registerUser(registerRequest);
        return ResponseEntity.ok(new SimpleResponse("Account created. " +
                "A verification letter has been sent to your e-mail address. " +
                "You will get access to the account after verification and " +
                "agreement with the administrator of the resource."));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request){
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<SimpleResponse> logoutUser(){
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<SimpleResponse> verifyAccount(@RequestParam("code") @NotBlank String verificationCode){
        authService.verifyAccount(verificationCode);
        return ResponseEntity.ok(new SimpleResponse("Verification was successful!"));
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<SimpleResponse> forgotPasswordUsersRequest(@RequestBody @Valid ForgotPasswordRequest request){
        authService.changePasswordRequest(request.getEmail());
        return ResponseEntity.ok(new SimpleResponse("A password recovery email has been sent to your e-mail address"));
    }

    @PostMapping("/reset")
    public ResponseEntity<SimpleResponse> resetPassword(@RequestParam("code") @NotBlank String passwordResetCode,
                                                        @RequestBody @Valid ChangePasswordRequest request){
        authService.changePassword(passwordResetCode, request.getPassword());
        return ResponseEntity.ok(new SimpleResponse("Success!"));
    }

    @GetMapping("/check_code")
    public ResponseEntity<SimpleResponse> checkResetPasswordCode(@RequestParam(value = "code")
                                                                     @NotBlank String passwordResetCode){
        authService.isActiveResetCode(passwordResetCode);
        return ResponseEntity.ok(new SimpleResponse("Code is active!"));
    }
}
