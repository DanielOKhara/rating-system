package com.okhara.rating_system.repository.jpa;

import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.model.rating.CommentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllBySellerIdAndStatus(Long sellerId, CommentStatus status, Pageable pageable);

    Optional<Comment> findByIdAndSellerIdAndStatus(Long id, Long sellerId, CommentStatus status);

    List<Comment> findAllByStatusIn(Set<CommentStatus> status, Pageable pageable);

    @Query("SELECT c FROM Comment c JOIN FETCH c.seller WHERE c.id = :id")
    Optional<Comment> findByIdWithSeller(@Param("id") Long id);
}