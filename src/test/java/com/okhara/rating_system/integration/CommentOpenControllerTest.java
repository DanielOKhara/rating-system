package com.okhara.rating_system.integration;

import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.RoleType;
import com.okhara.rating_system.model.rating.Comment;
import com.okhara.rating_system.model.rating.CommentStatus;
import com.okhara.rating_system.web.dto.request.comment.SimpleCommentRequest;
import com.okhara.rating_system.web.dto.request.comment.UpdateCommentRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentOpenControllerTest extends AbstractTest {

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

    @BeforeAll
    void setUp() {
        AppUser seller = userRepository.save(AppUser.builder()
                .nickname("seller1")
                .email("seller1@example.com")
                .password("password")
                .roles(Set.of(RoleType.ROLE_SELLER))
                .status(AccountStatus.ACTIVE)
                .emailVerified(true)
                .build());

        Comment comment1 = new Comment();
        comment1.setMessage("Great seller!");
        comment1.setSeller(seller);
        comment1.setGrade(5);
        comment1.setCreatedAt(Instant.now());
        comment1.setStatus(CommentStatus.APPROVED);
        comment1.setAnonymousToken("anon_token_1");

        Comment comment2 = new Comment();
        comment2.setMessage("Fast delivery");
        comment2.setSeller(seller);
        comment2.setGrade(5);
        comment2.setCreatedAt(Instant.now());
        comment2.setStatus(CommentStatus.APPROVED);
        comment2.setAnonymousToken("anon_token_1");



        commentRepository.saveAndFlush(comment1);
        commentRepository.saveAndFlush(comment2);
    }

    @Test
    void whenGetAllUsersComments_thenReturnCommentResponseList() throws Exception {
        mockMvc.perform(get("/sellers/1/comments")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments", hasSize(2)))
                .andExpect(jsonPath("$.comments[0].message").value("Fast delivery"))
                .andExpect(jsonPath("$.comments[1].message").value("Great seller!"));
    }

    @Test
    void whenCreateComment_thenReturnCommentWithTokenResponse() throws Exception {
        SimpleCommentRequest request = new SimpleCommentRequest(5, "Test to add comment");

        mockMvc.perform(post("/sellers/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(3))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void whenGetUsersCommentByCommentId_ThenReturnCommentResponse() throws Exception{
        mockMvc.perform(get("/sellers/1/comments/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(2))
                .andExpect(jsonPath("$.grade").value(5))
                .andExpect(jsonPath("$.message").value("Fast delivery"));
    }

    @Test
    void whenAuthorUpdateComment_thenReturnUpdatedCommentResponse() throws Exception {
        Long commentId = 2L;
        String updatedMessage = "Updated comment message";
        int updatedGrade = 4;

        Comment existedComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found in test!"));

        UpdateCommentRequest request = new UpdateCommentRequest(updatedMessage, updatedGrade);

        mockMvc.perform(put("/sellers/1/comments/" + commentId)
                        .cookie(new MockCookie("anonymousTokens", existedComment.getAnonymousToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(commentId))
                .andExpect(jsonPath("$.message").value(updatedMessage))
                .andExpect(jsonPath("$.grade").value(updatedGrade));
    }

    @Test
    void whenUpdateCommentWithInvalidData_thenReturnBadRequest() throws Exception {
        Long commentId = 2L;

        UpdateCommentRequest invalidRequest = new UpdateCommentRequest("", -1); // Пустой текст и недопустимая оценка

        mockMvc.perform(put("/sellers/1/comments/" + commentId)
                        .cookie(new MockCookie("anonymousTokens", "{}"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateCommentWithWrongToken_thenReturnForbidden() throws Exception {
        Long commentId = 2L;

        UpdateCommentRequest request = new UpdateCommentRequest("Valid message", 5);

        String wrongTokenJson = objectMapper.writeValueAsString(
                Map.of(commentId.toString(), "wrong_token")
        );

        mockMvc.perform(put("/sellers/1/comments/" + commentId)
                        .cookie(new MockCookie("anonymousTokens", wrongTokenJson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateNonExistingComment_thenReturnNotFound() throws Exception {
        Long nonExistingCommentId = 999L;

        UpdateCommentRequest request = new UpdateCommentRequest("Valid message", 5);

        mockMvc.perform(put("/sellers/1/comments/" + nonExistingCommentId)
                        .cookie(new MockCookie("anonymousTokens", "{}"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateCommentWithoutToken_thenReturnForbidden() throws Exception {
        Long commentId = 2L;

        UpdateCommentRequest request = new UpdateCommentRequest("Valid message", 5);

        mockMvc.perform(put("/sellers/1/comments/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenAuthorDeletesComment_thenNoContentResponse() throws Exception {
        Long commentId = 2L;
        Comment existedComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found in test!"));

        mockMvc.perform(delete("/sellers/1/comments/" + commentId)
                        .cookie(new MockCookie("anonymousTokens", existedComment.getAnonymousToken())))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteCommentWithInvalidToken_thenForbiddenResponse() throws Exception {
        Long commentId = 2L;
        String invalidTokenJson = objectMapper.writeValueAsString(Map.of(commentId.toString(), "invalid_token"));

        mockMvc.perform(delete("/sellers/1/comments/" + commentId)
                        .cookie(new MockCookie("anonymousTokens", invalidTokenJson)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenDeleteNonExistentComment_thenNotFoundResponse() throws Exception {
        mockMvc.perform(delete("/sellers/1/comments/999"))
                .andExpect(status().isNotFound());
    }
}
