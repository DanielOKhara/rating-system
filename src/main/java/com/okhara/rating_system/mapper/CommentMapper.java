package com.okhara.rating_system.mapper;

import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.model.rating.CommentStatus;
import com.okhara.rating_system.web.dto.request.comment.SimpleCommentRequest;
import com.okhara.rating_system.web.dto.response.comment.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CommentMapper {

    public Comment requestToEntity(SimpleCommentRequest request){
        return Comment.builder()
                        .message(request.getMessage())
                .grade(request.getGrade())
                .status(CommentStatus.PENDING)
                .anonymousToken(UUID.randomUUID().toString())
                .build();
    }

    public CommentResponse entityToResponse(Comment comment){
        return new CommentResponse(comment.getId(), comment.getGrade(), comment.getMessage(), comment.getCreatedAt());
    }

    public AdminCommentResponse entityToAdminResponse(Comment comment){
        AdminCommentResponse response = new AdminCommentResponse();
        response.setCommentId(comment.getId());
        response.setStatus(comment.getStatus().toString());
        response.setGrade(comment.getGrade());
        response.setMessage(comment.getMessage());
        response.setCommentedSellerId(comment.getSeller().getId());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }

    public AdminCommentListResponse commentsListToAdminCommentListResponse(List<Comment> comments){
        AdminCommentListResponse response = new AdminCommentListResponse();
        response.setComments(comments.stream().map(this::entityToAdminResponse).toList());
        return response;
    }

    public CommentWithTokenResponse entityToResponseWithToken(Comment comment){
        return new CommentWithTokenResponse(comment.getId(), comment.getAnonymousToken());
    }

    public CommentsListResponse commentsListToCommentsResponseList(List<Comment> comments){
        CommentsListResponse response = new CommentsListResponse();
        response.setComments(comments.stream().map(this::entityToResponse).toList());
        return response;
    }

}
