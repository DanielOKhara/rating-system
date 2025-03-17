package com.okhara.rating_system.service.open;

import com.okhara.rating_system.exception.CoordinationException;
import com.okhara.rating_system.exception.EntityNotExistException;
import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.RoleType;
import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.model.rating.CommentStatus;
import com.okhara.rating_system.repository.jpa.AppUserRepository;
import com.okhara.rating_system.repository.jpa.CommentRepository;
import com.okhara.rating_system.web.dto.request.comment.UpdateCommentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentOpenService {

    private final CommentRepository commentRepository;
    private final AppUserRepository userRepository;

    public List<Comment> findAllApprovedSellersComments(Long sellerId, Pageable pageable){
        return commentRepository.findAllBySellerIdAndStatus(sellerId, CommentStatus.APPROVED, pageable);
    }

    public Comment findApprovedCommentById(Long commentId, Long sellerId){
        return commentRepository.findByIdAndSellerIdAndStatus(commentId, sellerId, CommentStatus.APPROVED)
                .orElseThrow(() ->
                        new EntityNotExistException("Comment not exist or not approved yet!"));
    }

    @Transactional
    public Comment saveComment(Long sellerId, Comment comment){
        AppUser seller = userRepository.findByIdAndRolesContainingAndStatusIn(sellerId, RoleType.ROLE_SELLER,
                Set.of(AccountStatus.ACTIVE, AccountStatus.PLACEHOLDER)).orElseThrow(() ->
                new EntityNotExistException("Your comment rejected because you try to comment unavailable seller!"));
        comment.setSeller(seller);

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment saveWithPlaceholder(String nickname, Comment comment){
        if(userRepository.existsByNicknameIgnoreCase(nickname)){
            throw new CoordinationException(MessageFormat.format(
                    "User {0} already exist!", nickname));
        }
        AppUser placeholder = AppUser.builder()
                .nickname(nickname)
                .status(AccountStatus.PENDING)
                .roles(Set.of(RoleType.ROLE_SELLER))
                .build();
        userRepository.save(placeholder);

        comment.setStatus(CommentStatus.PENDING_WITH_PLACEHOLDER);
        comment.setSeller(placeholder);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Long commentId, String anonymousToken, UpdateCommentRequest request) {

        Comment comment = commentRepository.findByIdWithSeller(commentId)
                .orElseThrow(() -> new EntityNotExistException("Comment not found"));

        if (!Objects.equals(anonymousToken, comment.getAnonymousToken())) {
            throw new CoordinationException("You are not the owner of this comment!");
        }

        AppUser seller = comment.getSeller();
        seller.getRating().removeGrade(comment.getGrade());

        comment.setMessage(request.getNewMessage());
        comment.setGrade(request.getNewGrade());
        comment.setStatus(CommentStatus.PENDING);

        userRepository.save(seller);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, String anonymousToken) {

        Comment commentToDelete = commentRepository.findByIdWithSeller(commentId).orElseThrow(() ->
                new EntityNotExistException(MessageFormat.format("Comment with id {0} doesn't exist", commentId)));


        if (!Objects.equals(anonymousToken, commentToDelete.getAnonymousToken())) {
            throw new CoordinationException("You are not the owner of this comment!");
        }

        commentRepository.delete(commentToDelete);

        AppUser user = commentToDelete.getSeller();
        user.getRating().removeGrade(commentToDelete.getGrade());

        userRepository.save(user);
    }

}