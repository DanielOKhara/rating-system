package com.okhara.rating_system.service.open;

import com.okhara.rating_system.exception.EntityNotExistException;
import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.RoleType;
import com.okhara.rating_system.repository.jpa.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserOpenService {

    private static final EnumSet<AccountStatus> ALLOWED_STATUSES =
            EnumSet.of(AccountStatus.ACTIVE, AccountStatus.PLACEHOLDER);

    private final AppUserRepository userRepository;

    public List<AppUser> findAllActiveSellers() {
        return userRepository.findByRolesContainingAndStatusIn(RoleType.ROLE_SELLER,
                EnumSet.of(AccountStatus.ACTIVE));
    }

    public List<AppUser> findAllSellers(){
        return userRepository.findByRolesContainingAndStatusIn(RoleType.ROLE_SELLER,
                ALLOWED_STATUSES);
    }

    public AppUser findSellerById(Long id){
        return userRepository.findByIdAndRolesContainingAndStatusIn(id,
                RoleType.ROLE_SELLER,
                ALLOWED_STATUSES).orElseThrow(() ->
                new EntityNotExistException(
                        MessageFormat.format("Seller with id \"{0}\" does not exist", id)
                ));
    }

    public AppUser findSellerByNickname(String nickname){
        return userRepository.findByNicknameAndRolesContainingAndStatusIn(
                nickname,
                RoleType.ROLE_SELLER,
                ALLOWED_STATUSES).orElseThrow(() ->
                new EntityNotExistException(
                MessageFormat.format("Seller with nickname \"{0}\" does not exist", nickname)
        ));
    }
}
