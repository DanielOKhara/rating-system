package com.okhara.rating_system.mapper;

import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.model.rating.CommentStatus;
import com.okhara.rating_system.web.dto.request.comment.SimpleCommentRequest;
import com.okhara.rating_system.web.dto.response.comment.CommentResponse;
import com.okhara.rating_system.web.dto.response.comment.CommentWithTokenResponse;
import com.okhara.rating_system.web.dto.response.comment.CommentsListResponse;
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

    public CommentWithTokenResponse entityToResponseWithToken(Comment comment){
        return new CommentWithTokenResponse(comment.getId(), comment.getAnonymousToken());
    }

    public CommentsListResponse commentsListToCommentsResponseList(List<Comment> comments){
        CommentsListResponse response = new CommentsListResponse();
        response.setComments(comments.stream().map(this::entityToResponse).toList());
        return response;
    }

}
