package com.okhara.rating_system.repository.jpa;

import com.okhara.rating_system.model.marketplace.GameObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GameObjectRepository extends JpaRepository<GameObject, Long> {

    Page<GameObject> findAllByGameIdOrderByCreatedAtDesc(Long gameId, Pageable pageable);

    Page<GameObject> findAllBySellerIdOrderByCreatedAtDesc(Long sellerId, Pageable pageable);
}
