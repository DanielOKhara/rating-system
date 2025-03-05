package com.okhara.rating_system.repository;

import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.model.rating.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findBySellerIdAndStatus(Long sellerId, CommentStatus status);

    Optional<Comment> findByIdAndStatus(Long id, CommentStatus status);
}
