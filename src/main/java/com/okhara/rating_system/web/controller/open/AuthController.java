package com.okhara.rating_system.web.controller.open;

import com.okhara.rating_system.service.security.AuthService;
import com.okhara.rating_system.web.dto.request.auth.*;
import com.okhara.rating_system.web.dto.response.auth.AuthResponse;
import com.okhara.rating_system.web.dto.response.auth.RefreshTokenResponse;
import com.okhara.rating_system.web.dto.response.SimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication (Open controller)", description = "Endpoints for user authentication and account management.")
public class AuthController {

    private final AuthService authService;


    @Operation(summary = "Sign in", description = "Authenticates a user and returns a JWT token.")
    @ApiResponse(responseCode = "202", description = "User authenticated successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authUser(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.accepted().body(authService.authenticateUser(loginRequest));
    }

    @Operation(summary = "Register new user", description = "Registers a new seller account and sends a verification email.")
    @ApiResponse(responseCode = "200", description = "Account created successfully",
            content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
    @PostMapping("/register")
    public ResponseEntity<SimpleResponse> registerUser(@RequestBody @Valid RegisterSellerAccountRequest registerRequest){
        authService.registerUser(registerRequest);
        return ResponseEntity.ok(new SimpleResponse("Account created. " +
                "A verification letter has been sent to your e-mail address. " +
                "You will get access to the account after verification and " +
                "agreement with the administrator of the resource."));
    }


    @Operation(summary = "Refresh token", description = "Generates a new access token using a refresh token.")
    @ApiResponse(responseCode = "200", description = "New access token issued",
            content = @Content(schema = @Schema(implementation = RefreshTokenResponse.class)))
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request){
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @Operation(summary = "Logout user", description = "Logs out the current user and invalidates their refresh token.")
    @ApiResponse(responseCode = "200", description = "User logged out successfully")
    @PostMapping("/logout")
    public ResponseEntity<SimpleResponse> logoutUser(){
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Verify account", description = "Verifies a user's account using the provided verification code.")
    @ApiResponse(responseCode = "200", description = "Account verified successfully",
            content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
    @GetMapping("/verify")
    public ResponseEntity<SimpleResponse> verifyAccount(@RequestParam("code") @NotBlank String verificationCode){
        authService.verifyAccount(verificationCode);
        return ResponseEntity.ok(new SimpleResponse("Verification was successful!"));
    }

    @Operation(summary = "Forgot password", description = "Sends a password recovery email to the user.")
    @ApiResponse(responseCode = "200", description = "Password recovery email sent",
            content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
    @PostMapping("/forgot_password")
    public ResponseEntity<SimpleResponse> forgotPasswordUsersRequest(@RequestBody @Valid ForgotPasswordRequest request){
        authService.changePasswordRequest(request.getEmail());
        return ResponseEntity.ok(new SimpleResponse("A password recovery email has been sent to your e-mail address"));
    }

    @Operation(summary = "Reset password", description = "Resets the user's password using a reset code.")
    @ApiResponse(responseCode = "200", description = "Password reset successfully",
            content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
    @PostMapping("/reset")
    public ResponseEntity<SimpleResponse> resetPassword(@RequestParam("code") @NotBlank String passwordResetCode,
                                                        @RequestBody @Valid ChangePasswordRequest request){
        authService.changePassword(passwordResetCode, request.getPassword());
        return ResponseEntity.ok(new SimpleResponse("Success!"));
    }

    @Operation(summary = "Check reset code", description = "Checks if a password reset code is still valid.")
    @ApiResponse(responseCode = "200", description = "Code is active",
            content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
    @GetMapping("/check_code")
    public ResponseEntity<SimpleResponse> checkResetPasswordCode(@RequestParam(value = "code")
                                                                     @NotBlank String passwordResetCode){
        authService.isActiveResetCode(passwordResetCode);
        return ResponseEntity.ok(new SimpleResponse("Code is active!"));
    }
}
