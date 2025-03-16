package com.okhara.rating_system.unit;

import com.okhara.rating_system.exception.CoordinationException;
import com.okhara.rating_system.exception.EntityNotExistException;
import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.model.rating.CommentStatus;
import com.okhara.rating_system.model.rating.Rating;
import com.okhara.rating_system.repository.jpa.AppUserRepository;
import com.okhara.rating_system.repository.jpa.CommentRepository;
import com.okhara.rating_system.service.admin.AdminCommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminCommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private AdminCommentService adminCommentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void approveComment_Success() {
        // Arrange
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setGrade(5);
        comment.setStatus(CommentStatus.PENDING);

        AppUser seller = new AppUser();
        seller.setStatus(AccountStatus.ACTIVE);
        seller.setRating(new Rating());

        comment.setSeller(seller);

        when(commentRepository.findByIdWithSeller(commentId)).thenReturn(Optional.of(comment));

        // Act
        adminCommentService.approveComment(commentId);

        // Assert
        assertEquals(CommentStatus.APPROVED, comment.getStatus());
        assertEquals(1, seller.getRating().getCommentsCount());
        assertEquals(5, seller.getRating().getSumOfGrades());
    }

    @Test
    void approveComment_AlreadyApproved_ThrowsException() {
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setStatus(CommentStatus.APPROVED);

        when(commentRepository.findByIdWithSeller(commentId)).thenReturn(Optional.of(comment));

        assertThrows(CoordinationException.class, () -> adminCommentService.approveComment(commentId));
    }

    @Test
    void approveComment_CommentNotFound_ThrowsException() {
        Long commentId = 1L;
        when(commentRepository.findByIdWithSeller(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> adminCommentService.approveComment(commentId));
    }

    @Test
    void rejectComment_Success() {
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setStatus(CommentStatus.PENDING);

        when(commentRepository.findByIdWithSeller(commentId)).thenReturn(Optional.of(comment));

        adminCommentService.rejectComment(commentId);

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void rejectComment_Approved_ThrowsException() {
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setStatus(CommentStatus.APPROVED);

        when(commentRepository.findByIdWithSeller(commentId)).thenReturn(Optional.of(comment));

        assertThrows(CoordinationException.class, () -> adminCommentService.rejectComment(commentId));
    }

    @Test
    void rejectComment_CommentNotFound_ThrowsException() {
        Long commentId = 1L;
        when(commentRepository.findByIdWithSeller(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> adminCommentService.rejectComment(commentId));
    }
}