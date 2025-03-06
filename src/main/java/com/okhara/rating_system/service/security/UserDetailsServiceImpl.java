package com.okhara.rating_system.service.security;


import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.repository.jpa.AppUserRepository;
import com.okhara.rating_system.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        AppUser user = repository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found. Nickname is: " + nickname));
        return new AppUserDetails(user);
    }
}
