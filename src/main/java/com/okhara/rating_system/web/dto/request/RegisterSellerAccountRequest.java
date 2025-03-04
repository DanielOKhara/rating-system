package com.okhara.rating_system.web.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterSellerAccountRequest {

    private String nickname;

    private String password;

    private String email;

}
