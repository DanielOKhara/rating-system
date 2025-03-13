package com.okhara.rating_system.web.dto.request.comment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentRequest {

    @NotBlank(message = "Please, write something!")
    private String newMessage;

    @Min(value = 1, message = "Minimal grade is - 1")
    @Max(value = 5, message = "Maximal grade is - 5")
    private Integer newGrade;
}
