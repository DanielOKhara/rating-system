package com.okhara.rating_system.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatingGameRequest {

    @NotNull(message = "Game can't be created without title")
    @NotBlank(message = "Game title can't be empty")
    private String gameTitle;

}
