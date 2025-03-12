package com.okhara.rating_system.web.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Enter new password!")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[_\\-!():=])[a-zA-Zа-яА-Я0-9_\\-!():=]{6,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one special character (_-!():=)."
    )
    private String password;
}
