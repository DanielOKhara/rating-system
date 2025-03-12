package com.okhara.rating_system.web.controller.open;

import com.okhara.rating_system.mapper.CommentMapper;
import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.service.open.CommentOpenService;
import com.okhara.rating_system.web.dto.request.comment.CommentWithPlaceholderRequest;
import com.okhara.rating_system.web.dto.response.comment.CommentWithTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sellers/placeholder/comment")
@RequiredArgsConstructor
public class CommentPlaceholderController {

    private final CommentOpenService commentOpenService;
    private final CommentMapper commentMapper;

    @PostMapping
    public ResponseEntity<CommentWithTokenResponse> createCommentWithPlaceholder(
            @RequestBody @Valid CommentWithPlaceholderRequest request) {

        Comment commentFromRequest = commentMapper.requestToEntity(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                commentMapper.entityToResponseWithToken(commentOpenService.saveWithPlaceholder(request.getSellerNickname(),
                        commentFromRequest))
        );
    }
}
