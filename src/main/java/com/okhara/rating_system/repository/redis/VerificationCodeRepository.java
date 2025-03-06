package com.okhara.rating_system.repository.redis;

import com.okhara.rating_system.model.auth.VerificationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends CrudRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByCode(String code);

    void deleteByUserId(Long userId);
}
