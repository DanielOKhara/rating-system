package com.okhara.rating_system.web.controller.open;

import com.okhara.rating_system.mapper.CommentMapper;
import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.service.open.CommentOpenService;
import com.okhara.rating_system.web.dto.request.comment.CommentWithPlaceholderRequest;
import com.okhara.rating_system.web.dto.response.comment.CommentWithTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Comment Placeholder (Open controller)", description = "API for managing seller comments (without registration)")
@RequiredArgsConstructor
public class CommentPlaceholderController {

    private final CommentOpenService commentOpenService;
    private final CommentMapper commentMapper;


    @Operation(
            summary = "Create a comment for a seller",
            description = "Allows users to leave a comment for a seller, even if the seller's profile has not been created yet."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comment successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "A seller with this nickname already exists")
    })
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
