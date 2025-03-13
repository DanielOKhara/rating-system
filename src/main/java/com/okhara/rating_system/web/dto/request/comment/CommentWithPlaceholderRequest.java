package com.okhara.rating_system.web.dto.request.comment;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentWithPlaceholderRequest extends SimpleCommentRequest {

    @NotBlank(message = "Nickname can't be empty!")
    @Size(min = 4, max = 16, message = "Nickname must be between 4 and 16 characters")
    @Pattern(regexp = "^[a-zA-Z]\\w{3,15}$", message = "Only letters, numbers, and \"_\" are acceptable.")
    private String sellerNickname;

    public CommentWithPlaceholderRequest(Integer grade,
                                         String message,
                                         String sellerNickname) {
        super(grade, message);
        this.sellerNickname = sellerNickname;
    }
}
