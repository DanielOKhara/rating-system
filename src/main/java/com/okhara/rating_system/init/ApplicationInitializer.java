package com.okhara.rating_system.init;

import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.RoleType;
import com.okhara.rating_system.model.marketplace.Game;
import com.okhara.rating_system.repository.jpa.AppUserRepository;
import com.okhara.rating_system.repository.jpa.GameRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitializer {

    private final JdbcTemplate jdbcTemplate;
    private final AppUserRepository userRepository;
    private final GameRepository gameRepository;
    private static final List<String> DEFAULT_GAMES = List.of("CS:GO", "FIFA", "Dota 2", "Team Fortress");

    @Value("${init.enabled:true}")
    private boolean initEnabled;

    @PostConstruct
    public void initApp() {
        if(!initEnabled){
            return;
        }
            executeSchemaSQL();
            createAdminIfNotExists();
            createGamesIfNotExists();
    }

    private void executeSchemaSQL() {
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("data.sql"));
            log.info("Init script data.sql success");
        } catch (SQLException e) {
            log.error("Error for script data.sql {}", e.getMessage());
        }
    }

    private void createAdminIfNotExists() {
        Optional<AppUser> admin = userRepository.findByRolesContaining(RoleType.ROLE_ADMIN);
        if (admin.isEmpty()) {
            AppUser newAdmin = AppUser.builder()
                    .nickname("admin")
                    .email("danielokhara@gmail.com")
                    .password(new BCryptPasswordEncoder().encode("password")) // "password"
                    .roles(Set.of(RoleType.ROLE_ADMIN))
                    .status(AccountStatus.ACTIVE)
                    .emailVerified(true)
                    .createdAt(Instant.now())
                    .build();
            userRepository.save(newAdmin);
            log.info("Created admin: admin / danielokhara@gmail.com");
        } else {
            log.info("Admin already exist");
        }
    }

    private void createGamesIfNotExists(){
        List<Game> existedObjects = gameRepository.findAll();
        if(!existedObjects.isEmpty()){
            log.info("Games already initialized");
            return;
        }
        log.info("Starting games initializing...");

        List<Game> createdGames = DEFAULT_GAMES.stream()
                .map(nameOfGame -> Game.builder()
                        .name(nameOfGame)
                        .build()).toList();
        gameRepository.saveAll(createdGames);
        log.info("CS:GO, FIFA, Dota 2, Team Fortress - are added");
    }
}
