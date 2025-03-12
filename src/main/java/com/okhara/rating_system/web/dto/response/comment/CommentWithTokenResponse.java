package com.okhara.rating_system.web.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentWithTokenResponse {

    private Long commentId;
    private String token;
}
