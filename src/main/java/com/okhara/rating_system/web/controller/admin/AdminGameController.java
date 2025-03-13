package com.okhara.rating_system.web.controller.admin;

import com.okhara.rating_system.mapper.GameMapper;
import com.okhara.rating_system.service.admin.AdminGameService;
import com.okhara.rating_system.web.dto.request.CreatingGameRequest;
import com.okhara.rating_system.web.dto.response.game.GameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/create_game")
@Tag(name = "Admin game controller (Must be authenticated as ADMIN)", description = """
        This controller designed only for creating game entity!
        Users should know in advance about the deletion of an entity.
        Therefore, deletion is only possible in updates.
        """)
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class AdminGameController {

    private final AdminGameService adminGameService;
    private final GameMapper gameMapper;

    @PostMapping
    @Operation(summary = "Create game field", description = "Creating game field. Be careful with this entity!")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Game created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<GameResponse> createGame(@RequestBody @Valid CreatingGameRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                gameMapper.entityToResponse(adminGameService.createGame(request)));
    }
}
