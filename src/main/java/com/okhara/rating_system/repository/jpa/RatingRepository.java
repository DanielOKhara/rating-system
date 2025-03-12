package com.okhara.rating_system.repository.jpa;

import com.okhara.rating_system.model.rating.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("""
        SELECT r FROM Rating r
        WHERE r.commentsCount > 0
        ORDER BY r.rating DESC, r.commentsCount DESC
    """)
    Page<Rating> findTopSellers(Pageable pageable);
}
