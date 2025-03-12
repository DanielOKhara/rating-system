package com.okhara.rating_system.web.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterSellerAccountRequest {

    @NotBlank(message = "Nickname can't be empty!")
    @Size(min = 4, max = 16, message = "Nickname must be between 4 and 16 characters")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]$", message = "Only letters, numbers, and \"_\" are acceptable.")
    private String nickname;

    @NotBlank(message = "You can't create account without password!")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[_\\-!():=])[a-zA-Zа-яА-Я0-9_\\-!():=]{6,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one special character (_-!():=)."
    )
    private String password;

    @NotBlank(message = "E-mail must exist!")
    @Email(message = "Please, write correct e-mail!")
    private String email;

}
