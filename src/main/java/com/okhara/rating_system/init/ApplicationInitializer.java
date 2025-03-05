package com.okhara.rating_system.init;

import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.RoleType;
import com.okhara.rating_system.repository.AppUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ApplicationInitializer {

    private final JdbcTemplate jdbcTemplate;
    private final AppUserRepository userRepository;

    @Value("${init.enabled:true}")
    private boolean initEnabled;

    @PostConstruct
    public void initApp() {
        if(!initEnabled){
            return;
        }
            executeSchemaSQL();
            createAdminIfNotExists();
    }

    private void executeSchemaSQL() {
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("data.sql"));
            System.out.println("Init script data.sql success");
        } catch (SQLException e) {
            System.err.println("Error for script data.sql " + e.getMessage());
        }
    }

    private void createAdminIfNotExists() {
        Optional<AppUser> admin = userRepository.findByRolesContaining(RoleType.ROLE_ADMIN);
        if (admin.isEmpty()) {
            AppUser newAdmin = AppUser.builder()
                    .nickname("admin")
                    .email("danielokhara@gmail.com")
                    .password("{bcrypt}$2a$10$uYfJ2EJ9FQ6WklBf/yCvde6GZm/eYJaXujHn1qIh69N7XQkZz9VoC") // "password"
                    .roles(Set.of(RoleType.ROLE_ADMIN))
                    .status(AccountStatus.ACTIVE)
                    .emailVerified(true)
                    .createdAt(Instant.now())
                    .build();
            userRepository.save(newAdmin);
            System.out.println("Created admin: admin / danielokhara@gmail.com");
        } else {
            System.out.println("Admin already exist");
        }
    }
}
