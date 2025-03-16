package com.okhara.rating_system.integration;

import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.RoleType;
import com.okhara.rating_system.service.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AdminAppUserControllerTest extends AbstractTest {

    AppUser admin;
    AppUser pendingUser1;
    AppUser pendingUser2;

    @MockitoBean
    private EmailService emailService;

    @Container
    protected static PostgreSQLContainer<?> postgreSQLContainer;

    static {
        DockerImageName postgres = DockerImageName.parse("postgres:12.3");

        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer(postgres).withReuse(false);
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry){
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        System.out.println("Testcontainers JDBC URL: " + jdbcUrl);

        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.url", () -> jdbcUrl);
    }

    @BeforeEach
    void setUp() {
        admin = userRepository.saveAndFlush(AppUser.builder()
                .nickname("admin")
                .email("danielokhara@gmail.com")
                .password(new BCryptPasswordEncoder().encode("password"))
                .roles(Set.of(RoleType.ROLE_ADMIN))
                .status(AccountStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build());
        pendingUser1 = userRepository.saveAndFlush(AppUser.builder()
                .nickname("seller1")
                .email("seller1@example.com")
                .password("password")
                .roles(Set.of(RoleType.ROLE_SELLER))
                .status(AccountStatus.PENDING)
                .emailVerified(true)
                .build());
        pendingUser2 = userRepository.saveAndFlush(AppUser.builder()
                .nickname("seller2")
                .email("seller2@example.com")
                .password("password")
                .roles(Set.of(RoleType.ROLE_SELLER))
                .status(AccountStatus.PENDING)
                .emailVerified(true)
                .build());
        doNothing().when(emailService).sendRejectionEmail(any(AppUser.class));
        doNothing().when(emailService).sendConfirmationEmail(any(AppUser.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenGetAllPendingAccounts_thenReturnPendingSellersList() throws Exception {
        mockMvc.perform(get("/admin/pending_accounts")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers").isArray())
                .andExpect(jsonPath("$.sellers.length()").value(2));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsServiceImpl",
            value = "admin",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void whenActivateAccountByAdmin_thenReturnAcceptedStatus() throws Exception {
        pendingUser1 = userRepository.findById(pendingUser1.getId()).orElseThrow();
        System.out.println("Activating user ID: " + pendingUser1.getId());
        System.out.println("User status before activation: " + pendingUser1.getStatus());
        mockMvc.perform(post("/admin/pending_accounts/" + pendingUser1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "userDetailsServiceImpl",
            value = "admin",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void whenDeactivateAccountByAdmin_thenReturnNoContentStatus() throws Exception {
        mockMvc.perform(delete("/admin/pending_accounts/" + pendingUser2.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
