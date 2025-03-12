package com.okhara.rating_system.web.controller.admin;

import com.okhara.rating_system.mapper.CommentMapper;
import com.okhara.rating_system.service.admin.AdminCommentService;
import com.okhara.rating_system.web.dto.response.comment.CommentsListResponse;
import jakarta.validation.constraints.NotBlank;
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
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService adminCommentService;
    private final CommentMapper commentMapper;

    @GetMapping
    public ResponseEntity<CommentsListResponse> getAllPendingComments(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                                      @RequestParam(defaultValue = "10") @Positive int size){
        return ResponseEntity.ok(commentMapper.commentsListToCommentsResponseList(
                adminCommentService.getAllPendingComments(PageRequest.of(page, size))));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> approveComment(@PathVariable @NotBlank @Positive Long id){
        adminCommentService.approveComment(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> rejectComment(@PathVariable @NotBlank @Positive Long id){
        adminCommentService.rejectComment(id);
        return ResponseEntity.noContent().build();
    }

}
