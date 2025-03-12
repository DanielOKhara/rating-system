package com.okhara.rating_system.service.open;

import com.okhara.rating_system.exception.EntityNotExistException;
import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.RoleType;
import com.okhara.rating_system.model.rating.Rating;
import com.okhara.rating_system.repository.jpa.AppUserRepository;
import com.okhara.rating_system.repository.jpa.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppUserOpenService {

    private static final Set<AccountStatus> ALLOWED_STATUSES =
            Set.of(AccountStatus.ACTIVE, AccountStatus.PLACEHOLDER);

    private final AppUserRepository userRepository;
    private final RatingRepository ratingRepository;

    public List<AppUser> findAllActiveSellers(Pageable pageable) {
        return userRepository.findAllByRolesContainingAndStatusIn(RoleType.ROLE_SELLER,
                Set.of(AccountStatus.ACTIVE), pageable);
    }

    public List<AppUser> findAllSellers(Pageable pageable){
        return userRepository.findAllByRolesContainingAndStatusIn(RoleType.ROLE_SELLER,
                ALLOWED_STATUSES, pageable);
    }

    public AppUser findSellerById(Long id){
        return userRepository.findByIdAndRolesContainingAndStatusIn(id,
                RoleType.ROLE_SELLER,
                ALLOWED_STATUSES).orElseThrow(() ->
                new EntityNotExistException(
                        MessageFormat.format("Seller with id \"{0}\" does not exist", id)
                ));
    }

    public List<AppUser> findTopSellers(Pageable pageable) {
        return ratingRepository.findTopSellers(pageable)
                .map(Rating::getSeller)
                .toList();
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