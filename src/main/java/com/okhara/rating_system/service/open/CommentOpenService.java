package com.okhara.rating_system.service.open;

import com.okhara.rating_system.exception.EntityNotExistException;
import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.model.rating.CommentStatus;
import com.okhara.rating_system.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentOpenService {

    private final CommentRepository commentRepository;

    public List<Comment> findAllApprovedSellersComments(Long sellerId){
        return commentRepository.findBySellerIdAndStatus(sellerId, CommentStatus.APPROVED);
    }

    public Comment findApprovedCommentById(Long commentId){
        return commentRepository.findByIdAndStatus(commentId, CommentStatus.APPROVED)
                .orElseThrow(() ->
                        new EntityNotExistException("Comment not founded or not approved yet!"));
    }

    //todo глянь логику чуть позже
    public Comment save(Comment comment){
        comment.setStatus(CommentStatus.PENDING);
        Comment savedComment = commentRepository.save(comment);
        return savedComment;
    }


}