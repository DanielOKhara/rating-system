package com.okhara.rating_system.web.controller.open;

import com.okhara.rating_system.mapper.CommentMapper;
import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.service.open.CommentOpenService;
import com.okhara.rating_system.web.dto.request.comment.SimpleCommentRequest;
import com.okhara.rating_system.web.dto.request.comment.UpdateCommentRequest;
import com.okhara.rating_system.web.dto.response.comment.CommentResponse;
import com.okhara.rating_system.web.dto.response.comment.CommentWithTokenResponse;
import com.okhara.rating_system.web.dto.response.comment.CommentsListResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RestController
@RequestMapping("/sellers/{sellerId}/comments")
@RequiredArgsConstructor
public class CommentOpenController {

    private final CommentOpenService commentService;
    private final CommentMapper commentMapper;


    @GetMapping
    public ResponseEntity<CommentsListResponse> getAllUsersComments(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                                    @RequestParam(defaultValue = "10") @Positive int size,
                                                                    @PathVariable @NotNull @Positive Long sellerId){
        return ResponseEntity.ok(commentMapper.commentsListToCommentsResponseList(
                commentService.findAllApprovedSellersComments(sellerId,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created_at")))));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getUsersCommentByCommentId(@PathVariable @NotNull @Positive Long sellerId,
                                                                      @PathVariable @NotNull @Positive Long commentId){
        return ResponseEntity.ok(commentMapper.entityToResponse(
                commentService.findApprovedCommentById(commentId, sellerId)));
    }

    @PostMapping
    public ResponseEntity<CommentWithTokenResponse> createComment(@PathVariable @NotNull @Positive Long sellerId,
                                                                           @RequestBody @Valid SimpleCommentRequest request){
        Comment createdComment = commentService.saveComment(sellerId, commentMapper.requestToEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentMapper.entityToResponseWithToken(createdComment));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable @NotNull @Positive Long commentId,
                                                         @CookieValue(value = "anonymousTokens", required = false) String anonymousTokensJson,
                                                         @RequestBody @Valid UpdateCommentRequest request){
        Comment updatedComment = commentService.updateComment(commentId, anonymousTokensJson, request);
        return ResponseEntity.ok(commentMapper.entityToResponse(updatedComment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable @NotNull @Positive Long commentId,
                                              @CookieValue(value = "anonymousTokens", required = false) String anonymousTokensJson){
        commentService.deleteComment(commentId, anonymousTokensJson);
        return ResponseEntity.noContent().build();
    }
}
