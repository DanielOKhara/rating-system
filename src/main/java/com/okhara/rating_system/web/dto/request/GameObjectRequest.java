package com.okhara.rating_system.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameObjectRequest {

    @NotBlank(message = "Title must exist")
    @Size(min = 4, max = 256, message = "Title size: min - 4, max - 256 symbols!")
    private String title;

    @NotBlank(message = "Description must exist")
    @Size(min = 4, max = 256, message = "Description size: min - 4, max - 256 symbols!")
    private String description;
}
