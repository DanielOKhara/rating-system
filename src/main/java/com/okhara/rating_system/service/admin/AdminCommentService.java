package com.okhara.rating_system.service.admin;

import com.okhara.rating_system.aop.AuditLoggable;
import com.okhara.rating_system.exception.CoordinationException;
import com.okhara.rating_system.exception.EntityNotExistException;
import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.model.rating.CommentStatus;
import com.okhara.rating_system.repository.jpa.AppUserRepository;
import com.okhara.rating_system.repository.jpa.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminCommentService {

    private final CommentRepository commentRepository;
    private final AppUserRepository userRepository;

    public List<Comment> getAllPendingComments(Pageable pageable) {
        return commentRepository.findAllByStatusIn(Set.of(CommentStatus.PENDING, CommentStatus.PENDING_WITH_PLACEHOLDER),
                pageable);
    }

    @AuditLoggable
    @Transactional
    public void approveComment(Long commentId) {
        Comment commentForApprove = commentRepository.findByIdWithSeller(commentId).orElseThrow(() ->
                new EntityNotExistException(MessageFormat.format("Comment with id {0} doesn't exist", commentId)));
        if (commentForApprove.getStatus() == CommentStatus.APPROVED) {
            throw new CoordinationException("Comment with id:" + commentId + " already approved!");
        }
        if (commentForApprove.getStatus() == CommentStatus.PENDING_WITH_PLACEHOLDER) {
            commentForApprove.getSeller().setStatus(AccountStatus.PLACEHOLDER);
        }
        commentForApprove.getSeller().getRating().addGrade(commentForApprove.getGrade());
        commentForApprove.setStatus(CommentStatus.APPROVED);
    }

    @AuditLoggable
    @Transactional
    public void rejectComment(Long commentId) {
        Comment commentForApprove = commentRepository.findByIdWithSeller(commentId).orElseThrow(() ->
                new EntityNotExistException(MessageFormat.format("Comment with id {0} doesn't exist", commentId)));
        if (commentForApprove.getStatus() == CommentStatus.APPROVED) {
            throw new CoordinationException("Comment with id:" + commentId + " already approved!");
        }
        if (commentForApprove.getStatus() == CommentStatus.PENDING_WITH_PLACEHOLDER) {
            userRepository.delete(commentForApprove.getSeller());
        }
        commentRepository.delete(commentForApprove);
    }

    //todo: глянь, оно вообще нужно?
//    @Transactional
//    public void deleteComment(Long commentId) {
//        Comment commentToDelete = commentRepository.findByIdWithSeller(commentId).orElseThrow(() ->
//                new EntityNotExistException(MessageFormat.format("Comment with id {0} doesn't exist", commentId)));
//
//        AppUser user = commentToDelete.getSeller();
//        user.getRating().removeGrade(commentToDelete.getGrade());
//
//        userRepository.save(user);
//        commentRepository.deleteById(commentId);
//    }
}