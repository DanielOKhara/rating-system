package com.okhara.rating_system.web.controller.open;

import com.okhara.rating_system.mapper.CommentMapper;
import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.service.open.CommentOpenService;
import com.okhara.rating_system.web.dto.request.comment.SimpleCommentRequest;
import com.okhara.rating_system.web.dto.request.comment.UpdateCommentRequest;
import com.okhara.rating_system.web.dto.response.comment.CommentResponse;
import com.okhara.rating_system.web.dto.response.comment.CommentWithTokenResponse;
import com.okhara.rating_system.web.dto.response.comment.CommentsListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Public Comments (Open controller)", description = "Operations for retrieving and managing seller comments")
@RequiredArgsConstructor
public class CommentOpenController {

    private final CommentOpenService commentService;
    private final CommentMapper commentMapper;


    @Operation(summary = "Retrieve all approved comments for a seller",
            description = "Returns a paginated list of approved comments for the specified seller.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comments"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Seller not found")
    })
    @GetMapping
    public ResponseEntity<CommentsListResponse> getAllUsersComments(
            @RequestParam(defaultValue = "0") @PositiveOrZero @Parameter(description = "Page number (zero-based)") int page,
            @RequestParam(defaultValue = "10") @Positive @Parameter(description = "Number of comments per page") int size,
            @PathVariable @NotNull @Positive @Parameter(description = "Seller ID") Long sellerId){
        return ResponseEntity.ok(commentMapper.commentsListToCommentsResponseList(
                commentService.findAllApprovedSellersComments(sellerId,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))));
    }


    @Operation(summary = "Retrieve a specific approved comment",
            description = "Fetches an approved comment by ID for the given seller.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the comment"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getUsersCommentByCommentId(@PathVariable @NotNull @Positive Long sellerId,
                                                                      @PathVariable @NotNull @Positive Long commentId){
        return ResponseEntity.ok(commentMapper.entityToResponse(
                commentService.findApprovedCommentById(commentId, sellerId)));
    }

    @Operation(summary = "Submit a new comment",
            description = "Allows an anonymous user to submit a comment for a seller. Returns the created comment with a unique token.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comment successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<CommentWithTokenResponse> createComment(@PathVariable @NotNull @Positive Long sellerId,
                                                                           @RequestBody @Valid SimpleCommentRequest request){
        Comment createdComment = commentService.saveComment(sellerId, commentMapper.requestToEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentMapper.entityToResponseWithToken(createdComment));
    }


    @Operation(summary = "Update an existing comment",
            description = "Allows an anonymous user to update their comment using a stored token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access (invalid token)"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable @NotNull @Positive Long commentId,
                                                         @CookieValue(value = "anonymousTokens", required = false)
                                                         @Parameter(
                                                                 name = "anonymousTokens",
                                                                 in = ParameterIn.COOKIE,
                                                                 description = "Anonymous token for identifying user session"
                                                         )
                                                         String anonymousTokens,
                                                         @RequestBody @Valid UpdateCommentRequest request){
        Comment updatedComment = commentService.updateComment(commentId, anonymousTokens, request);
        return ResponseEntity.ok(commentMapper.entityToResponse(updatedComment));
    }


    @Operation(summary = "Delete a comment",
            description = "Allows an anonymous user to delete their comment using a stored token.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access (invalid token)"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable @NotNull @Positive Long commentId,
                                              @CookieValue(value = "anonymousTokens", required = false) String anonymousTokensJson){
        commentService.deleteComment(commentId, anonymousTokensJson);
        return ResponseEntity.noContent().build();
    }
}
