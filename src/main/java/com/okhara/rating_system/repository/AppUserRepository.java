package com.okhara.rating_system.repository;

import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    List<AppUser> findByRolesContainingAndStatusIn(RoleType role, Set<AccountStatus> statuses);

    Optional<AppUser> findByIdAndRolesContainingAndStatusIn(Long id, RoleType role, Set<AccountStatus> statuses);

    Optional<AppUser> findByNicknameAndRolesContainingAndStatusIn(String nickname, RoleType role,
                                                                  Set<AccountStatus> statuses);

    Optional<AppUser> findByRoles(RoleType role);

    Optional<AppUser> findByNickname(String nickname);

    Boolean existsByNicknameIgnoreCase(String nickname);

    Boolean existsByEmail(String email);
}