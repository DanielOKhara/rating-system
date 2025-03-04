package com.okhara.rating_system.service.admin;

import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.RoleType;
import com.okhara.rating_system.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAppUserService {

    private final AppUserRepository userRepository;

    public List<AppUser> getAllPendingAccounts(){
        return userRepository.findByRolesContainingAndStatusIn(
                RoleType.ROLE_SELLER,
                EnumSet.of(AccountStatus.PENDING));
    }



}
