package com.okhara.rating_system.service.admin;

import com.okhara.rating_system.aop.AuditLoggable;
import com.okhara.rating_system.exception.EntityNotExistException;
import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.RoleType;
import com.okhara.rating_system.repository.jpa.AppUserRepository;
import com.okhara.rating_system.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAppUserService {

    private final AppUserRepository userRepository;
    private final EmailService emailService;

    public List<AppUser> getAllPendingAccounts(Pageable pageable){
        return userRepository.findAllByRolesContainingAndStatusInAndEmailIsNotNull(
                RoleType.ROLE_SELLER,
                Set.of(AccountStatus.PENDING), pageable);
    }

    @AuditLoggable
    @Transactional
    public AppUser activateSellersAccount(Long id){
        AppUser sellerForActivate = userRepository.findByIdAndStatus(id, AccountStatus.PENDING).orElseThrow(
                () -> new EntityNotExistException("Account does not exist or already activated!")
        );

        sellerForActivate.setStatus(AccountStatus.ACTIVE);
        emailService.sendConfirmationEmail(sellerForActivate);

        log.info("Seller {} id: {} registration success", sellerForActivate.getNickname(), sellerForActivate.getId());

        return sellerForActivate;
    }

    @AuditLoggable
    public void deactivatePendingAccount(Long id){
        AppUser sellerForActivate = userRepository.findByIdAndStatus(id, AccountStatus.PENDING).orElseThrow(
                () -> new EntityNotExistException("Account does not exist or already activated!")
        );
        userRepository.delete(sellerForActivate);

        emailService.sendRejectionEmail(sellerForActivate);
        log.info("Account {} deleted. ", sellerForActivate.getNickname());
    }
}
