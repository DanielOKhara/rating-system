package com.okhara.rating_system.web.controller.admin;

import com.okhara.rating_system.mapper.CommentMapper;
import com.okhara.rating_system.service.admin.AdminCommentService;
import com.okhara.rating_system.web.dto.response.comment.AdminCommentListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/pending_comments")
@Tag(name = "Admin comments controller (Must be authenticated as ADMIN)", description = "Approve/reject pending comments")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService adminCommentService;
    private final CommentMapper commentMapper;

    @GetMapping
    @Operation(summary = "Get all pending comments", description = "Return all pending comments list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of pending comments retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AdminCommentListResponse> getAllPendingComments(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                                          @RequestParam(defaultValue = "10") @Positive int size){
        return ResponseEntity.ok(commentMapper.commentsListToAdminCommentListResponse(
                adminCommentService.getAllPendingComments(PageRequest.of(page, size))));
    }

    @PostMapping("/{id}")
    @Operation(summary = "Approve pending comment", description = "Approve a pending comment. If the comment contains placeholders (e.g., auto-detected mentions), they will be replaced with real data.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Comment approved"),
            @ApiResponse(responseCode = "400", description = "Invalid comment ID"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "409", description = "Comment is already approved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> approveComment(@PathVariable @NotNull @Positive Long id){
        adminCommentService.approveComment(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Reject pending comment", description = "Rejects a pending comment by its ID. If the comment is associated with a placeholder seller, the seller will also be removed.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment rejected successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid comment ID"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "409", description = "Comment is already approved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> rejectComment(@PathVariable @NotNull @Positive Long id){
        adminCommentService.rejectComment(id);
        return ResponseEntity.noContent().build();
    }

}
