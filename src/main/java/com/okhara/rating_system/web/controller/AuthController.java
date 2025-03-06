package com.okhara.rating_system.web.controller;

import com.okhara.rating_system.service.security.AuthService;
import com.okhara.rating_system.web.dto.request.LoginRequest;
import com.okhara.rating_system.web.dto.request.RefreshTokenRequest;
import com.okhara.rating_system.web.dto.request.RegisterSellerAccountRequest;
import com.okhara.rating_system.web.dto.response.AuthResponse;
import com.okhara.rating_system.web.dto.response.RefreshTokenResponse;
import com.okhara.rating_system.web.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authUser(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.accepted().body(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<SimpleResponse> registerUser(@RequestBody RegisterSellerAccountRequest registerRequest){
        authService.registerUser(registerRequest);
        return ResponseEntity.ok(new SimpleResponse("Account created. " +
                "A verification letter has been sent to your e-mail address. " +
                "You will get access to the account after verification and " +
                "agreement with the administrator of the resource."));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<SimpleResponse> logoutUser(@AuthenticationPrincipal UserDetails userDetails){
        authService.logout();
        return ResponseEntity.ok(new SimpleResponse(MessageFormat.format("User {0} logout.",
                userDetails.getUsername())));
    }

    @GetMapping("/verify")
    public ResponseEntity<SimpleResponse> verifyAccount(@RequestParam("code") String verificationCode){

        //todo: добавь хэндлер толковый
        authService.verifyAccount(verificationCode);
        return ResponseEntity.ok(new SimpleResponse("Verification was successful!"));
    }
}
