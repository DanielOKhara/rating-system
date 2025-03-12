package com.okhara.rating_system.repository.redis;

import com.okhara.rating_system.model.auth.ResetPasswordCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordCodeRepository extends CrudRepository<ResetPasswordCode, Long> {

    Optional<ResetPasswordCode> findByCode(String code);

    void deleteByUserId(Long userId);
}
