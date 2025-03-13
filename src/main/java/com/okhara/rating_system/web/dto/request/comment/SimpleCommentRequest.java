package com.okhara.rating_system.web.dto.request.comment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCommentRequest {

    @Min(value = 1, message = "Minimal grade is - 1")
    @Max(value = 5, message = "Maximal grade is - 5")
    @NotNull
    private Integer grade;

    @NotBlank(message = "Please, write something!")
    private String message;

}
