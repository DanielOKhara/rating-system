package com.okhara.rating_system.web.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;

    private Byte grade;

    private String message;

    private Instant createdAt;
}
