package com.okhara.rating_system.repository.jpa;

import com.okhara.rating_system.model.marketplace.GameObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameObjectRepository extends JpaRepository<GameObject, Long> {

}
